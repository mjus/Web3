package dao;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() {
        List<BankClient> list = new ArrayList<>();
        try(PreparedStatement stmt = connection.prepareStatement("select * from bank_client");
            ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                BankClient bankClient = new BankClient();
                bankClient.setId(result.getLong(1));
                bankClient.setName(result.getString(2));
                bankClient.setPassword(result.getString(3));
                bankClient.setMoney(result.getLong(4));
                list.add(bankClient);
            }
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        if (name != null && password != null) {
            BankClient bc = getClientByName(name);
            if (bc != null && bc.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        try(PreparedStatement stmt
                    = connection.prepareStatement("update bank_client set money=money+'" + transactValue + "' where name = '" + name + "'")) {
            stmt.executeUpdate();
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        try(Statement stmt = connection.createStatement();
            ResultSet result = stmt.getResultSet()) {
            stmt.executeQuery("select * from bank_client where id='" + id + "'");

            result.next();
            BankClient bankClient = new BankClient(result.getLong(1), result.getString(2),
                    result.getString(3), result.getLong(4));
            return bankClient;
        }
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        if (getClientByName(name).getMoney() >= expectedSum) {
            return true;
        }
        return false;
    }

    public long getClientIdByName(String name) throws SQLException {
        try(PreparedStatement pStmt = connection.prepareStatement("select * from bank_client where name = ?")) {
            pStmt.setString(1, name);
            ResultSet resultSet = pStmt.executeQuery();
            long id = 0;
            while (resultSet.next()) {
                id = resultSet.getLong("id");
            }
            return id;
        }
    }

    public BankClient getClientByName(String name) throws SQLException {
        try(PreparedStatement pStmt = connection.prepareStatement("select * from bank_client where name = ?")) {
            pStmt.setString(1, name);
            ResultSet resultSet = pStmt.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String nameRs = resultSet.getString("name");
                String password = resultSet.getString("password");
                Long money = resultSet.getLong("money");
                return new BankClient(id, nameRs, password, money);
            }
            return null;
        }
    }

    public void addClient(BankClient client) throws SQLException {
        try(Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("insert into bank_client (id, name, password, money) values (null , '"
                    + client.getName() + "' , '" + client.getPassword() + "' , '" + client.getMoney() + "')");
        }
    }

    public boolean isNameExiste(BankClient client) throws SQLException {
        try(Statement stmt = connection.createStatement()) {
            stmt.execute("select * from bank_client where name='" + client.getName() + "'");
            if (stmt.getResultSet().next()) {
                return true;
            }
            return false;
        }
    }

    public void deleteClient(String name) throws SQLException {
        try(Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("delete from bank_client where name='" + name + "'");
        }
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}