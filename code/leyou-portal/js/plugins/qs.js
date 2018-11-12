const qs = {
    //对escape()编码的字符串进行解码
    getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;//如果此处只写return;则返回的是undefined
    }
};