package le.com.search.repository;

import le.com.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: cuzz
 * @Date: 2018/11/10 15:47
 * @Description:
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long>{
}
