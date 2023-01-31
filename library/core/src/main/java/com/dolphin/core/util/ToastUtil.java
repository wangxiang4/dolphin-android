package com.dolphin.core.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dolphin.core.R;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 吐司工具
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/8
 */
@UtilityClass
public class ToastUtil {

    public void showBottom(final CharSequence text) {
        ToastUtils.make()
                .setLeftIcon(R.drawable.icon_app)
                .setMode(ToastUtils.MODE.DARK)
                .setNotUseSystemToast()
                .setDurationIsLong(false)
                .show(text);
    }

    public void showCenter(final CharSequence text) {
        ToastUtils.make()
                .setGravity(Gravity.CENTER, 0, 0)
                .setLeftIcon(R.drawable.icon_app)
                .setMode(ToastUtils.MODE.DARK)
                .setNotUseSystemToast()
                .setDurationIsLong(false)
                .show(text);
    }

    public void showTop(final CharSequence text) {
        ToastUtils.make()
                .setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                .setLeftIcon(R.drawable.icon_app)
                .setMode(ToastUtils.MODE.DARK)
                .setNotUseSystemToast()
                .setDurationIsLong(false)
                .show(text);
    }

    public void showActivityToast(Context context, final CharSequence text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

	public void showAmapError(Context context, int rCode){
		try {
	        switch (rCode) {
	        //服务错误码
            case 1001:
                throw new AMapException(AMapException.AMAP_SIGNATURE_ERROR);
            case 1002:
                throw new AMapException(AMapException.AMAP_INVALID_USER_KEY);
            case 1003:
                throw new AMapException(AMapException.AMAP_SERVICE_NOT_AVAILBALE);
            case 1004:
                throw new AMapException(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT);
            case 1005:
                throw new AMapException(AMapException.AMAP_ACCESS_TOO_FREQUENT);
            case 1006:
                throw new AMapException(AMapException.AMAP_INVALID_USER_IP);
            case 1007:
                throw new AMapException(AMapException.AMAP_INVALID_USER_DOMAIN);
            case 1008:
                throw new AMapException(AMapException.AMAP_INVALID_USER_SCODE);
            case 1009:
                throw new AMapException(AMapException.AMAP_USERKEY_PLAT_NOMATCH);
            case 1010:
                throw new AMapException(AMapException.AMAP_IP_QUERY_OVER_LIMIT);
            case 1011:
                throw new AMapException(AMapException.AMAP_NOT_SUPPORT_HTTPS);
            case 1012:
                throw new AMapException(AMapException.AMAP_INSUFFICIENT_PRIVILEGES);
            case 1013:
            	throw new AMapException(AMapException.AMAP_USER_KEY_RECYCLED);
            case 1100:
                throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_ERROR);
            case 1101:
                throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR);
            case 1102:
            	throw new AMapException(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT);
            case 1103:
            	throw new AMapException(AMapException.AMAP_ENGINE_RETURN_TIMEOUT);  
            case 1200:
                throw new AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS);
            case 1201:
                throw new AMapException(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS);
            case 1202:
                throw new AMapException(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST);
            case 1203:
                throw new AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR);
            //sdk返回错误
            case 1800:
            	throw new AMapException(AMapException.AMAP_CLIENT_ERRORCODE_MISSSING);
            case 1801:
            	throw new AMapException(AMapException.AMAP_CLIENT_ERROR_PROTOCOL);
            case 1802:
            	throw new AMapException(AMapException.AMAP_CLIENT_SOCKET_TIMEOUT_EXCEPTION);
            case 1803:
            	throw new AMapException(AMapException.AMAP_CLIENT_URL_EXCEPTION);
            case 1804:
            	throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWHOST_EXCEPTION);
            case 1806:
            	throw new AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION);
            case 1900:
            	throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
            case 1901:
            	throw new AMapException(AMapException.AMAP_CLIENT_INVALID_PARAMETER);
            case 1902:
            	throw new AMapException(AMapException.AMAP_CLIENT_IO_EXCEPTION);
            case 1903:
            	throw new AMapException(AMapException.AMAP_CLIENT_NULLPOINT_EXCEPTION);
              //云图和附近错误码  
            case 2000:
                throw new AMapException(AMapException.AMAP_SERVICE_TABLEID_NOT_EXIST);
            case 2001:
                throw new AMapException(AMapException.AMAP_ID_NOT_EXIST);
            case 2002:
                throw new AMapException(AMapException.AMAP_SERVICE_MAINTENANCE);
            case 2003:
            	throw new AMapException(AMapException.AMAP_ENGINE_TABLEID_NOT_EXIST);
            case 2100:
                throw new AMapException(AMapException.AMAP_NEARBY_INVALID_USERID);
            case 2101:
                throw new AMapException(AMapException.AMAP_NEARBY_KEY_NOT_BIND);
            case 2200:
                throw new AMapException(AMapException.AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR);
            case 2201:
                throw new AMapException(AMapException.AMAP_CLIENT_USERID_ILLEGAL);
            case 2202:
                throw new AMapException(AMapException.AMAP_CLIENT_NEARBY_NULL_RESULT);
            case 2203:
                throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_TOO_FREQUENT);
            case 2204:
                throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_LOCATION_ERROR);
            //路径规划   
            case 3000:
                throw new AMapException(AMapException.AMAP_ROUTE_OUT_OF_SERVICE);
            case 3001:
                throw new AMapException(AMapException.AMAP_ROUTE_NO_ROADS_NEARBY);
            case 3002:
                throw new AMapException(AMapException.AMAP_ROUTE_FAIL);
            case 3003:
            	throw new AMapException(AMapException.AMAP_OVER_DIRECTION_RANGE);
            //短传分享
	        case 4000:
	        	throw new AMapException(AMapException.AMAP_SHARE_LICENSE_IS_EXPIRED);
	        case 4001:
	        	throw new AMapException(AMapException.AMAP_SHARE_FAILURE); 
	        default:
	        	Toast.makeText(context,"查询失败："+rCode , Toast.LENGTH_LONG).show();
                LogUtils.i("查询失败", rCode,
                        "高德地图错误码查询地址:http://lbs.amap.com/api/android-sdk/guide/map-tools/error-code/");
                break;
	        }
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            LogUtils.e("查询失败", rCode,
                    "高德地图错误码查询地址:http://lbs.amap.com/api/android-sdk/guide/map-tools/error-code/");
        }
	}

}
