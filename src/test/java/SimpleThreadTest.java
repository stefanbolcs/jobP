import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;

import static org.mockito.Mockito.mock;

public class SimpleThreadTest {


    @Mock
    private final Statement statement;

    @Mock
    final Connection conn;

    @InjectMocks
    final Datasource datasource;



    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

            }


            @Test
    public void testMockDbConnection() throws Exception {
        Mockito.when(conn.createStatement()).thenReturn(statement);
        Mockito.when(conn.createStatement().executeUpdate(Mockito.any())).thenReturn(1);
            }
}
