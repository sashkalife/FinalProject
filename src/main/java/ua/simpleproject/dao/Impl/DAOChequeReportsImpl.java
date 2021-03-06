package ua.simpleproject.dao.Impl;

import org.apache.log4j.Logger;
import ua.simpleproject.authentification.Authentification;
import ua.simpleproject.dao.DAOChequeReports;
import ua.simpleproject.entity.ChequeReports;
import ua.simpleproject.exception.ConnectionException;
import ua.simpleproject.exception.DAOException;
import ua.simpleproject.transactions.ConnectionWrapper;
import ua.simpleproject.transactions.TransactionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DAOChequeReportsImpl implements DAOChequeReports {
    private Logger logger = Logger.getLogger(DAOChequeReportsImpl.class);
    public static final String SQL_INSERT = "INSERT INTO check_reports " +
            "(user_id, number_of_product, check_amount, time_close) VALUES (?,?,?,NOW())";
    public static final String SQL_SELECT = "SELECT* FROM check_reports " +
            "where time_close > ?";
    public static final String SQL_SELECT_BY_ID = "SELECT* FROM check_reports " +
            "where user_id = ? and time_close > ?";

    public static final String SQL_SELECT_LAST = "SELECT * FROM check_reports ORDER BY id DESC LIMIT 1";


    protected DAOChequeReportsImpl(){
    }

    /** insert object ChequeReport into database
     *
     * @param chequeReports show information of the Cheque
     * @throws DAOException this is own exception that combines exceptions which
     * happened during work with database
     */
    public void create(ChequeReports chequeReports) throws DAOException {
        try(ConnectionWrapper connection = TransactionManager.getConnection()) {
            PreparedStatement prStatement = connection.preparedStatement(SQL_INSERT);
            prStatement.setInt(1, chequeReports.getUserId());// userId
            prStatement.setInt(2, chequeReports.getNumberOfProduct());//count
            prStatement.setBigDecimal(3, chequeReports.getChequeAmount());//checkAmount
            prStatement.executeUpdate();
        } catch (SQLException | ConnectionException e) {
            throw new DAOException("Method DAOChequeReportsImpl.create has thrown an exception.", e);
        }
    }

    /** select information from DB according to current user and data
     *
     * @param currentTime
     * @param userID
     * @return list of objects ChequeReports
     * @throws DAOException this is own exception that combines exceptions which
     * happened during work with database
     */
    public List<ChequeReports> read(LocalDate currentTime , int userID) throws DAOException {
        List<ChequeReports> chequeReportsList = new ArrayList<>();
        java.sql.Date sqlDate = java.sql.Date.valueOf( currentTime );

        try(ConnectionWrapper connection = TransactionManager.getConnection()) {
            PreparedStatement prStatement = connection.preparedStatement(SQL_SELECT_BY_ID);
            prStatement.setInt(1, userID);
            prStatement.setDate(2, sqlDate);
            ResultSet resultSet = prStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int userId = resultSet.getInt(2);
                int count = resultSet.getInt(3);
                BigDecimal checkAmount = resultSet.getBigDecimal(4);
                Timestamp timeClose = resultSet.getTimestamp(5);
                ChequeReports chequeReports = new ChequeReports(id, userId, count, checkAmount, timeClose);
                chequeReportsList.add(chequeReports);
            }
        } catch (SQLException | ConnectionException e) {
            throw new DAOException(String.format("Method read(user id: '%d') has thrown an exception.", userID), e);
        }
        return chequeReportsList;

    }
    /** select information from DB according to current user and data
     *
     * @param zReportTime
     * @return list of objects ChequeReports
     * @throws DAOException this is own exception that combines exceptions which
     * happened during work with database
     */
    public List<ChequeReports> read(Timestamp zReportTime) throws DAOException {
        List<ChequeReports> chequeReportsList = new ArrayList<>();
        try(ConnectionWrapper connection = TransactionManager.getConnection()) {
            PreparedStatement prStatement = connection.preparedStatement(SQL_SELECT);
            prStatement.setTimestamp(1, zReportTime);
            ResultSet resultSet = prStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int userId = resultSet.getInt(2);
                int count = resultSet.getInt(3);
                BigDecimal checkAmount = resultSet.getBigDecimal(4);
                Timestamp timeClose = resultSet.getTimestamp(5);
                ChequeReports chequeReports = new ChequeReports(id, userId, count, checkAmount, timeClose);
                chequeReportsList.add(chequeReports);
            }
        } catch (SQLException | ConnectionException e) {
            throw new DAOException("Method read() has thrown an exception.", e);
        }
        return chequeReportsList;

    }
    public ChequeReports readLast() throws DAOException {
        ChequeReports chequeReports = null;

        try(ConnectionWrapper connection = TransactionManager.getConnection()) {
            PreparedStatement prStatement = connection.preparedStatement(SQL_SELECT_LAST);
            ResultSet resultSet = prStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                int userId = resultSet.getInt(2);
                int count = resultSet.getInt(3);
                BigDecimal checkAmount = resultSet.getBigDecimal(4);
                Timestamp timeClose = resultSet.getTimestamp(5);
                chequeReports = new ChequeReports(id, userId, count, checkAmount, timeClose);
            }
        } catch (SQLException | ConnectionException e) {
            throw new DAOException("Method readLast() has thrown an exception.", e);
        }
        return chequeReports;

    }
}
