/*
 * Copyright (c) JForum Team
 * All rights reserved.

 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:

 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Created on 24/05/2004 17:40:25
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.jforum.JForumExecutionContext;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: AutoKeys.java,v 1.11 2007/10/01 14:26:43 rafaelsteil Exp $
 */
public class AutoKeys
{
	private String autoGeneratedKeysQuery;

	protected boolean supportAutoGeneratedKeys()
	{
		return SystemGlobals.getBoolValue(ConfigKeys.DATABASE_AUTO_KEYS);
	}

	/**
	 * @param query The query to execute to retrieve the last generated key
	 */
	protected void setAutoGeneratedKeysQuery(final String query)
	{
		this.autoGeneratedKeysQuery = query;
	}

	protected String getAutoGeneratedKeysQuery()
	{
		return this.autoGeneratedKeysQuery;
	}

	protected String getErrorMessage()
	{
		return "Could not obtain the latest generated key. This error may be associated"
			+ " to some invalid database driver operation or server failure."
			+ " Please check the database configurations and code logic.";
	}

	protected PreparedStatement getStatementForAutoKeys(final String queryName, final Connection conn) throws SQLException
	{
		PreparedStatement pstmt = null;
		
		if (this.supportAutoGeneratedKeys()) {
			pstmt = conn.prepareStatement(SystemGlobals.getSql(queryName), Statement.RETURN_GENERATED_KEYS);
		}
		else {
			pstmt = conn.prepareStatement(SystemGlobals.getSql(queryName));
		}

		return pstmt;
	}

	protected PreparedStatement getStatementForAutoKeys(final String queryName) throws SQLException
	{
		return this.getStatementForAutoKeys(queryName, JForumExecutionContext.getConnection());
	}

	protected int executeAutoKeysQuery(final PreparedStatement pstmt) throws SQLException
	{
		return this.executeAutoKeysQuery(pstmt, JForumExecutionContext.getConnection());
	}

	protected int executeAutoKeysQuery(PreparedStatement pstmt, final Connection conn) throws SQLException
	{
		int id = -1;
		pstmt.executeUpdate();

		ResultSet resultSet = null;
		
		try {
			if (this.supportAutoGeneratedKeys()) {
				resultSet = pstmt.getGeneratedKeys();
				
				if (resultSet.next()) {
					id = resultSet.getInt(1);
				}
			}
			else {
				pstmt = conn.prepareStatement(this.getAutoGeneratedKeysQuery());
				resultSet = pstmt.executeQuery();

				if (resultSet.next()) {
					id = resultSet.getInt(1);
				}
			}
		}
		finally {
			DbUtils.close(resultSet);
		}

		if (id == -1) {
			throw new SQLException(this.getErrorMessage());
		}

		return id;
	}
}
