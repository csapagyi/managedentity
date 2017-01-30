package com.thisismydesign.managedentity;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;

import java.util.List;

/**
 * This class supports paginating DB entries based on custom criteria.
 *
 * <p>Notes:
 * <ul>
 * <li>always specify an order
 * <li>entries are paginated in descending order (last one first) to ensure point (a) below
 * </ul>
 *
 * <p>While iterating:
 * <ul>
 * <li>(a) removing records that fit the pagination criteria may result in some records being returned multiple times but every record will be returned. With an idempotent implementation this will not cause any problems
 * <li>(b) adding records that fit the pagination criteria may result in some records not being returned therefore should be avoided
 * <li>(c) modifying records that fit the pagination criteria in a way that the criteria fields are modified may result in some records not being returned or being returned multiple times and therefore should be avoided
 * </ul>
 */
public class Paginator {

    private final int paginateBy;
    private final int maxResults;
    private int firstResult;
    private boolean finished;
    private int numberOfEntriesToBeQueried;
    private final Criteria criteria;

    /**
     * Get {@link Paginator} object that will query entries matching {@code criteria} by the number of {@code
     * paginateBy}.
     *
     * @param criteria criteria for entries to be queried
     * @param paginateBy maximum number of entries returned in one iteration
     */
    public Paginator(Criteria criteria, int paginateBy) {
        this.paginateBy = paginateBy;
        this.criteria = criteria;
        finished = false;

        maxResults = fetchMaxResults(criteria);

        firstResult = maxResults - paginateBy;
        numberOfEntriesToBeQueried = paginateBy;
    }

    /**
     * Get next {@code paginateBy} piece of data matching {@code criteria}.
     *
     * @return next chuck of data
     */
    public List<?> getNext() {
        return getNext(criteria);
    }

    /**
     * Get next {@code paginateBy} piece of data matching provided {@code criteria}.
     *
     * <p>Should only be used if the original criteria's session may gets closed during Pagination.
     * The same criteria must be provided as to the constructor.
     *
     * @param criteria criteria for entries to be queried (same as in constructor)
     * @return next chuck of data
     */
    public List<?> getNext(Criteria criteria) {
        if(!finished) {
            List<?> result = fetchResults(criteria, firstResult, numberOfEntriesToBeQueried);

            if (firstResult <= 0) finished = true;
            firstResult -= paginateBy;
            if (firstResult < 0) {
                numberOfEntriesToBeQueried = paginateBy + firstResult;
                firstResult = 0;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Get total number of records matching {@code criteria}.
     *
     * @return total number of records matching criteria
     */
    public int getMaxResults() {
        return maxResults;
    }

    private int fetchMaxResults(Criteria criteria) {

        ScrollableResults results = criteria.scroll();
        results.last();
        Integer count = results.getRowNumber() + 1;
        results.close();

        return count;
    }

    private List<?> fetchResults(Criteria criteria, int firstResult, int maxResults) {
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);

        return criteria.list();
    }
}
