/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2002, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

/**
 * The match listener
 */
public interface MatchListener
{
	/** 
     * The match details has been changed
     * 
     * @param match The new match details
     */
    public void matchUpdated(Match match);
 }
