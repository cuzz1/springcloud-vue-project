import com.leyou.seckill.vo.SeckillParameter;

import com.leyou.utils.JsonUtils;
import org.junit.Test;

import java.util.Date;

/**
 * @Author: 98050
 * @Time: 2018-11-13 21:36
 * @Feature:
 */
public class JsonTest {

    @Test
    public void json(){
        SeckillParameter seckillParameter = new SeckillParameter();
        seckillParameter.setEndTime(new Date());
        System.out.println(JsonUtils.serialize(seckillParameter));
    }
}
