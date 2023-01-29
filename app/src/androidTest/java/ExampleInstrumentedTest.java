import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *<p>
 * Instrumentation test，它将在Android设备上执行。 安卓插桩测试单元测试
 * 具体请参考官网: http://d.android.com/tools/testing
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/9/26
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.dolphin.core.test", appContext.getPackageName());
    }

}
