import com.example.learn.Reverse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyTest {

    Reverse reverse;

    @BeforeEach
    void setup() {
        reverse = new Reverse();
    }

    @Test
    @DisplayName("Ensures the name is properly reversed")
    public void testReverseName() {
        assertEquals("olleh", reverse.reverseName("hello"), "should print out the reverse");
        assertNotEquals("hi", reverse.reverseName("hi"), "should not print out the original argument name");
    }
}