package com.thisismydesign.managedentity;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;

import java.util.List;

public class Paginator {

    private final int paginateBy;
    private final int maxResults;
    private int firstResult;
    private boolean finished;
    private int numberOfEntriesToBeQueried;
    private final Criteria criteria;

    public Paginator(Criteria criteria, int paginateBy) {
        this.paginateBy = paginateBy;
        this.criteria = criteria;
        finished = false;

        maxResults = fetchMaxResults(criteria);

        firstResult = maxResults - paginateBy;
        numberOfEntriesToBeQueried = paginateBy;
    }

    public List<?> getNext() {
        return getNext(criteria);
    }

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
