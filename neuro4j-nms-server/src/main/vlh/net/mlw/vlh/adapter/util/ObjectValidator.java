/*
 * Created on 28.1.2005 azachar
 */
package net.mlw.vlh.adapter.util;

import net.mlw.vlh.ValueListInfo;

/**
 * This class is used in <code>ScrollableResultsDecorator</code> and
 * <code>ResultSetDecorator</code>. Using this validator you can enable or
 * disable putting objects into the final ResultsSet, thus into your final
 * valueList.
 * <p>
 * <li>
 * <em>Using validators is recommended in the case when you cannot do a filtering
 * in database tier, otherwise validators may greatly slow down a performance because it's
 * done in JVM.</em>
 * </li>
 * </p>
 * 
 * @see net.mlw.vlh.adapter.jdbc.AbstractJdbcAdapter#setValidator(ObjectValidator)
 * @see net.mlw.vlh.adapter.hibernate.Hibernate20Adapter#setValidator(ObjectValidator)
 * @see net.mlw.vlh.adapter.jdbc.objectWrapper.DefaultWrapperAdapter#setWrapper(ObjectWrapper)
 * @see net.mlw.vlh.adapter.hibernate.Hibernate20Adapter#setFocusOptimalization(boolean)
 * 
 * @author Andrej Zachar
 * @version $Revision: 1.3 $ $Date: 2005/02/23 13:54:14 $
 */
public interface ObjectValidator
{
    /**
     * 
     * @param objectToBeChecked
     * @return true if the given object is accepted at the final valueList.
     *         false if the given object is rejected from the final valueList.
     */
    public boolean isAcceptable(Object objectToBeChecked);

    /**
     * Adapter will set ValueListInfo before use. This interface allow you to
     * "send" some additional date that could be helpfull in "acceptable
     * process".
     * 
     * @param info
     */
    public void setValueListInfo(ValueListInfo info);
}
