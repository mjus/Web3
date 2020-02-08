package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<BankClient> getAllClient() throws SQLException {
        return getBankClientDAO().getAllBankClient();
    }

    public boolean deleteClient(String name) throws SQLException {
        getBankClientDAO().deleteClient(name);
        return true;
    }

    public boolean addClient(BankClient client) {
        try {
            if (!getBankClientDAO().isNameExiste(client)
                    && !getBankClientDAO().validateClient(client.getName(), client.getPassword())) {
                getBankClientDAO().addClient(client);
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        Connection connection = getMysqlConnection();
        try {
            connection.setAutoCommit(false);
            BankClientDAO bankClientDAO = getBankClientDAO();
            if (bankClientDAO.isNameExiste(sender) && bankClientDAO.isClientHasSum(sender.getName(), value)) {
                bankClientDAO.updateClientsMoney(sender.getName(), sender.getPassword(), (- 1) * value);
                bankClientDAO.updateClientsMoney(getClientByName(name).getName(), getClientByName(name).getPassword(), value);
                connection.commit();
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException | SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {}
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=root");       //password

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
