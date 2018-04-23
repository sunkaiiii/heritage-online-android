package com.example.sunkai.heritage.Data

class BaiduLoacationResponse {
    /**
     * address : CN|北京|北京|None|CHINANET|1|None
     * content : {"address":"北京市","address_detail":{"city":"北京市","city_code":131,"district":"","province":"北京市","street":"","street_number":""},"point":{"x":"116.39564504","y":"39.92998578"}}
     * status : 0
     */

    var address: String? = null
    var content: ContentBean? = null
    var status: Int = 0

    class ContentBean {
        /**
         * address : 北京市
         * address_detail : {"city":"北京市","city_code":131,"district":"","province":"北京市","street":"","street_number":""}
         * point : {"x":"116.39564504","y":"39.92998578"}
         */

        var address: String? = null
        var address_detail: AddressDetailBean? = null

        class AddressDetailBean {
            /**
             * city : 北京市
             * city_code : 131
             * district :
             * province : 北京市
             * street :
             * street_number :
             */

            var city: String? = null
            var city_code: Int = 0
            var district: String? = null
            var province: String? = null
            var street: String? = null
            var street_number: String? = null
        }
    }
}