package le.com.search.pojo;

import java.util.Map;

/**
 * @Author: cuzz
 * @Date: 2018/11/12 14:47
 * @Description: 用于接收json数据的对象
 */
public class SearchRequest {
    private String key;// 搜索条件

    private Integer page;// 当前页

    public Map<String, String> getFilter() {
        return filter;
    }

    private Map<String, String> filter;

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
