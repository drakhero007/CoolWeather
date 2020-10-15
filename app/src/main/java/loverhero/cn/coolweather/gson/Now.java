package loverhero.cn.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 鲍日鑫 on 2020/6/22.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }

}
