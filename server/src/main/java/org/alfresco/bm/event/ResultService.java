/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.bm.event;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Service interface providing methods to store and query for event results.
 * 
 * @author Derek Hulley
 * @since 1.0
 */
public interface ResultService
{
    /**
     * @return                  a string giving the location of the raw data
     */
    String getDataLocation();
    
    /**
     * Simply store an event for later use
     * 
     * @param result            the result to store
     */
    void recordResult(EventRecord result);
    
    /**
     * Retrieve the first result by start time
     * 
     * @return                  the first result or <tt>null</tt> if there are none
     */
    EventRecord getFirstResult();
    
    /**
     * Retrieve the last result by start time
     * 
     * @return                  the last result or <tt>null</tt> if there are none
     */
    EventRecord getLastResult();
    
    /**
     * Retrieve a page of event results by event name
     * 
     * @param eventName         the name of the event as recorded
     * @param skip              the number of results to skip (for paging)
     * @param limit             the number of results to retrieve
     * @return                  Returns any recorded results with the given name
     */
    List<EventRecord> getResults(String eventName, int skip, int limit);
    
    /**
     * Retrieve a page of event results using a time window
     * 
     * @param startTime         the first event time (inclusive, milliseconds)
     * @param eventName         the name of the event to use or <tt>null</tt> for all event names
     * @param successOrFail     <tt>true</tt> for success only or false for failures (<tt>null</tt> for all)
     */
    List<EventRecord> getResults(
            long startTime,
            String eventName,
            Boolean successOrFail,
            int skip, int limit);
    
    /**
     * Callback handler for aggregated results
     * 
     * @author derekh
     * @since 1.3
     */
    public interface ResultHandler
    {
        /**
         * The callback of statistics for a given time window.
         * 
         * @param fromTime      the start of the time window (inclusive)
         * @param toTime       the end of the time window (inclusive)
         * @param statsByEventName  statistics for the time window keyed by event name
         * @return              <tt>true</tt> to continue processing otherwise <tt>false</tt>
         * @throws              all exceptions will be handled
         */
        boolean processResult(
                long fromTime,
                long toTime,
                Map<String, DescriptiveStatistics> statsByEventName) throws Throwable;
        
        /**
         * Called when there are no more results to get the time to wait before polling for
         * more results.
         * 
         * @return              Return the time, in milliseconds, to wait before polling for
         *                      more results.  Use 0 or less to indicate that the process must
         *                      terminate immediately.
         */
        long getWaitTime();
    }
    
    /**
     * Get result statistics for discrete time intervals.
     * <p/>
     * A callback will be received for every time period requested.
     * 
     * @param handler           the client callback implementation
     * @param startTime         the start of the first results (inclusive, milliseconds).
     *                          The first reporting window will be moved to the first event after
     *                          this time and reset to a multiple of the <tt>windowSize</tt>.
     * @param eventName         the name of the event to use or <tt>null</tt> for all event names
     * @param successOrFail     <tt>true</tt> for success only or false for failures (<tt>null</tt> for all)
     * @param windowSize        the length (milliseconds) of a time window
     * @param chartOnly         <tt>true</tt> if only {@link EventRecord#isChart() chartable} results must be retrieved
     */
    void getResults(
            ResultHandler handler,
            long startTime,
            String eventName,
            Boolean successOrFail,
            long windowSize,
            boolean chartOnly);

    /**
     * Get a discrete list of event names from across all the results
     */
    List<String> getEventNames();
    
    /**
     * Count the number of events
     * 
     * @return                  the number of completed events
     */
    long countResults();
    
    /**
     * Count the number of previously-completed events with the given name.
     * 
     * @param name              the name of the event as recored
     * @return                  the count
     */
    long countResultsByEventName(String name);
    
    /**
     * @return                  the number of successfully completed events
     */
    long countResultsBySuccess();
    
    /**
     * @return                  the number of failed events
     */
    long countResultsByFailure();
}
