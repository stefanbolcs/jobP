import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleThreadTest {


    @Mock
    PreparedStatement stmt;

    @Mock
    Connection conn;


    @Mock
     DataSource ds;

    @Mock
    private ResultSet rs;

    private User u;





    @Before
    public void setUp() throws Exception{
        assertNotNull(ds);
       // MockitoAnnotations.initMocks(this);

        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(conn);


    }


            @Test
    public void testMockDbConnection() throws Exception {



        }
}
