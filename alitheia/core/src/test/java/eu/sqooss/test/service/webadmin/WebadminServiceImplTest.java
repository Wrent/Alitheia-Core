package eu.sqooss.test.service.webadmin;

import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Adam Kucera
 */
public class WebadminServiceImplTest {
    
    static WebadminServiceImpl impl;
    
    @BeforeClass
    public static void setUp() {
        impl = new WebadminServiceImpl();
    }

    @Test
    public void testWebadminServiceImpl() {
        assertNotNull(impl);
    }
    
}
