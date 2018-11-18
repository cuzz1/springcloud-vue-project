package le.com.search.client;


import com.leyou.item.pojo.Category;
import le.com.LySearchApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:48
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories() {
        List<Category> categories = categoryClient.queryCategoryListByids(Arrays.asList(1L, 2L, 3L));
        for (Category category : categories) {
            System.out.println(category);
        }
    }
}