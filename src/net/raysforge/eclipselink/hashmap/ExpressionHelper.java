package net.raysforge.eclipselink.hashmap;

import java.util.List;

import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;

public class ExpressionHelper {
	
	/**
	 * Extract the field or constant value from the comparison expression.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object extractValue(Expression expression, DatabaseQuery query) {
		Object value = null;
		expression.getBuilder().setSession(query.getSession());
		if (expression.isQueryKeyExpression()) {
			QueryKeyExpression queryKeyExpression = (QueryKeyExpression) expression;
			value = queryKeyExpression.getField();
			if ((queryKeyExpression.getMapping() != null) && queryKeyExpression.getMapping().getDescriptor().isDescriptorTypeAggregate()) {
				String name = queryKeyExpression.getField().getName();
				while (queryKeyExpression.getBaseExpression().isQueryKeyExpression()
						&& (((QueryKeyExpression) queryKeyExpression.getBaseExpression()).getMapping().isAbstractCompositeObjectMapping() || ((QueryKeyExpression) queryKeyExpression.getBaseExpression()).getMapping().isAbstractCompositeCollectionMapping() || ((QueryKeyExpression) queryKeyExpression
								.getBaseExpression()).getMapping().isAbstractCompositeDirectCollectionMapping())) {
					queryKeyExpression = (QueryKeyExpression) queryKeyExpression.getBaseExpression();
					if (queryKeyExpression.getMapping().isAbstractCompositeObjectMapping()) {
						name = ((AbstractCompositeObjectMapping) queryKeyExpression.getMapping()).getField().getName() + "." + name;
					} else if (queryKeyExpression.getMapping().isAbstractCompositeCollectionMapping()) {
						name = ((AbstractCompositeCollectionMapping) queryKeyExpression.getMapping()).getField().getName() + "." + name;
					} else if (queryKeyExpression.getMapping().isAbstractCompositeDirectCollectionMapping()) {
						name = ((AbstractCompositeDirectCollectionMapping) queryKeyExpression.getMapping()).getField().getName() + "." + name;
					}
				}
				DatabaseField field = new DatabaseField();
				field.setName(name);
				value = field;
			}
		} else if (expression.isFieldExpression()) {
			value = ((FieldExpression) expression).getField();
		} else if (expression.isConstantExpression()) {
			value = ((ConstantExpression) expression).getValue();
			if (((ConstantExpression) expression).getLocalBase() != null) {
				value = ((ConstantExpression) expression).getLocalBase().getFieldValue(value, query.getSession());
			}
		} else if (expression.isParameterExpression()) {
			value = query.getTranslationRow().get(((ParameterExpression) expression).getField());
			if (((ParameterExpression) expression).getLocalBase() != null) {
				value = ((ParameterExpression) expression).getLocalBase().getFieldValue(value, query.getSession());
			}
		} else {
			throw new EISException("Query too complex, comparison of [" + expression + "] not supported in query: " + query);
		}
		if (value instanceof List) {
			List values = (List) value;
			for (int index = 0; index < values.size(); index++) {
				Object element = values.get(index);
				if (element instanceof Expression) {
					element = extractValue((Expression) element, query);
					values.set(index, element);
				}
			}
		}
		return value;
	}

}
