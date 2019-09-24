package Controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos?createDatabaseIfNotExist=true&allowMultiQueries=true","root","mysql");
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
            ResultSet resultSet = pstm.executeQuery();

            if(!resultSet.next()){
                String sql="CREATE TABLE `customer` (\n" +
                        "  `id` varchar(10) NOT NULL,\n" +
                        "  `name` varchar(50) DEFAULT NULL,\n" +
                        "  `address` varchar(50) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1" +
                        "   CREATE TABLE `item` (\n" +
                        "  `itemCode` varchar(10) NOT NULL,\n" +
                        "  `description` varchar(50) DEFAULT NULL,\n" +
                        "  `qtyOnHand` int(11) DEFAULT NULL,\n" +
                        "  `unitPrice` int(11) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`itemCode`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
                        " CREATE TABLE `orderdetail` (\n" +
                        "  `orderId` varchar(10) DEFAULT NULL,\n" +
                        "  `itemCode` varchar(10) DEFAULT NULL,\n" +
                        "  `qty` int(11) DEFAULT NULL,\n" +
                        "  `unitPrice` int(11) DEFAULT NULL,\n" +
                        "  KEY `fk_orderId` (`orderId`),\n" +
                        "  KEY `fk_itemCode` (`itemCode`),\n" +
                        "  CONSTRAINT `FK_orderdetail` FOREIGN KEY (`orderId`) REFERENCES `orders` (`orderId`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                        "  CONSTRAINT `fk_itemCode` FOREIGN KEY (`itemCode`) REFERENCES `item` (`itemCode`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;" +
                        " CREATE TABLE `orders` (\n" +
                        "  `orderId` varchar(10) NOT NULL,\n" +
                        "  `orderDate` text,\n" +
                        "  `customerId` varchar(10) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`orderId`),\n" +
                        "  KEY `fk_customerId` (`customerId`),\n" +
                        "  CONSTRAINT `FK_orders` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

                PreparedStatement pst = connection.prepareStatement(sql);
                 pst.execute();
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance(){
        return  dbConnection = ((dbConnection==null) ? new DBConnection(): dbConnection);
    }

    public Connection getConnection(){
        return connection;
    }
}
