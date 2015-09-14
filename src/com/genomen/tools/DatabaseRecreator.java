package com.genomen.tools;

import com.genomen.core.Configuration;
import com.genomen.dao.DAOFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


/**
 * Convenience class for recreating the database
 * @author ciszek
 */
public class DatabaseRecreator {

    public static void recreateDatabase( String args) {

        if (Configuration.getConfiguration().getDBType() == DAOFactory.DERBY ) {
            deleteDatabase();
        }
        


        String commandFilePath = args;
        
        List<String> commands = getCommands(commandFilePath);

            Connection connection = null;
            try {
                connection = createConnection( Configuration.getConfiguration().getDatabaseAddress() );
                Statement statement;
                statement = connection.createStatement();

                for ( int i = 0; i < commands.size(); i++) {
                    statement.addBatch(commands.get(i));
                }
                statement.executeBatch();
                statement.close();
            }
            catch (SQLException ex) {
                System.out.println(ex);
            }
            finally {

                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }

            }
        System.out.println( commands.size() + " tables created");
    }

    private static Connection createConnection( String address ) {

        Connection connection = null;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection("jdbc:derby:" + address + ";create=true");

        }
        catch (SQLException ex) {

        }
        catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        catch (InstantiationException ex) {
            System.out.println(ex);
        }
        catch (IllegalAccessException ex) {
            System.out.println(ex);
        }

        return connection;
    }
    
    private static List<String> getCommands(String commandFilePath) {
        
        List<String> commands = new LinkedList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(commandFilePath));
            StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
            streamTokenizer.wordChars( '_','_' );
            streamTokenizer.wordChars( ';',';' );
            streamTokenizer.wordChars( ',',',' );
            streamTokenizer.wordChars( ',',',' );
            streamTokenizer.wordChars( '(','(' );
            streamTokenizer.wordChars( ')',')' );
             streamTokenizer.wordChars( '\'','\'' );

            StringBuilder stringBuilder = new StringBuilder();

            while( streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                //Semicolon is used to separate SQL commands
                if ( streamTokenizer.ttype == StreamTokenizer.TT_WORD && streamTokenizer.sval.matches("\\W+;") ) {
                    //Add the current token to the build String
                    stringBuilder.append(streamTokenizer.sval);
                    //Remove the semicolon
                    stringBuilder.deleteCharAt( stringBuilder.length()-1);
                    //Add the command to the list of commands and start building a new command.
                    commands.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
                else {
                    if (streamTokenizer.ttype == StreamTokenizer.TT_WORD) {
                        stringBuilder.append(streamTokenizer.sval);
                    }
                    else {
                        stringBuilder.append(streamTokenizer.nval);
                    }

                    stringBuilder.append(" ");
                }
            }

        }
        catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }
        
        return commands;
    }


    private static void deleteDatabase() {
        deleteFolder(new File(Configuration.getConfiguration().getDatabaseAddress()));
    }

    private static void deleteFolder(File path) {

        if( path.exists() ) {

            File[] files = path.listFiles();

            for(int i=0; i < files.length; i++) {

                if(files[i].isDirectory()) {
                    deleteFolder(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
            System.out.println("\tDeleting folder: " + path.getAbsolutePath() );
            path.delete();
        }
    }

}
