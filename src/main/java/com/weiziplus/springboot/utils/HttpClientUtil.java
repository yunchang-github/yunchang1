package com.weiziplus.springboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wanglongwei
 * @data 2019/7/12 10:45
 */
@Slf4j
public class HttpClientUtil {

    public static String post(String url, List<NameValuePair> params) {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(2000).setConnectTimeout(2000).setSocketTimeout(2000).build()).build();
        try {
            HttpPost post = new HttpPost(url);
            if (null != params) {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
                post.setEntity(urlEncodedFormEntity);
            }
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            post.setHeader("Accept", "Accept: text/plain, */*");
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
            post.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
            post.addHeader("X-Requested-With", "XMLHttpRequest");
            post.addHeader("origin", "https://sellercentral.amazon.com");
            post.addHeader("cookie", "session-id=141-2868426-3703719; ubid-main=130-3894210-6133449; s_pers=%20s_dl%3D1%7C1562730304935%3B%20gpv_page%3DUS%253ASC%253A%2520SellerCentralLogin%7C1562730304940%3B%20s_ev15%3D%255B%255B%2527Typed/Bookmarked%2527%252C%25271562728504942%2527%255D%255D%7C1720581304942%3B; sid=\"TjelSELexzTN9jq666LSzQ==|h0CFBCSYi7ZknfmX2ShzlN7hNbIDYvrfgvNWKopTJf4=\"; sp-cdn=\"L5Z9:CN\"; session-id-time=2082787201l; session-token=\"lpH3kIeQv0Jv4XcVktTq0iFGZ0FZkUbpOnCWrvw2884ATmu47jWSBEYYIrt1BXjL49+/Kg9xUWr2/jIhOXHCJ4vO3IfD5Uh3KHc5ZTXPdbijbx1gsQbMsb9bhJQpkwfBklKVuZFI2bH7uvtib8ENcFvHnk95bFWscx6+TBWVNTu3FHzoBi6gqul9LAETica0K8bg8F5aRVu21cT8xNyZhof/QE4g8kx/6U72cTd3tvw=\"; at-main=Atza|IwEBIMfEpou0YMETZUFsYoDozz9HwYHcb9kbpFhNaQr3N0qaNckQS-9OsLe-1L8WeszCQHG0980EFkK7FLQs44j5dzxv2vCFEOeSBkBqCTTVdMauGSPNCKhZOeXIb7NQwhKQB-HfMgIRcJuGMg_5vgBFRDslitZQJUeKeAzGpjilC_tqkvtpSElzwIp6R1WpO5DPPxjfxE8idu7OKp0ucIoohzIvQfC-ic-NqbaiSc0ySAEDe51v8DNY5-C3EwjND7EHNRKTwc0PkUJbpFzDYHGgBqoQqZej3-Ib1Y2feIDty4YwFVEXNXcvq52EkIydagUXdzlSQi0AjfWsXiSslZYeETi-TdPeR6ECuRqsUx3cNI4Er9ZUXgf0Zbzh0aBjqhxOUBpYcmHwZXRPPzudBH20-QWA; sess-at-main=\"Qpssazbe7piCfxCeXnag46f6AM/ALm43Wi9akt/06hw=\"; sst-main=Sst1|PQE7HO25PyVHzh7pX7BNAZ2WC7BViLPet95w8YvqQ-edcjT15b05IXl-t1Mcx-__weFbbNuaoKWCTGvRUlwpXpZ1kOr7-H6-cofvYYliIe06Y8mODrW5b0i_RV7r2WyP5wTyEWNwTUQaewajTQXKr5ORs6ek5rFmVEEU3SZVv7b25mzvZk26kAd8uQSl9nsIcyWRYdwwZq5QNewm6SQLAzJUJlzlrj1vRvTAaXKgLmZr7N9ZukJIU3UmgMmrLkXMP_GheO44h39a59q4nVu2iBPE3XSUC8kqdQCCkizx13tybPwRs-Kt7PXGrgjjxmj87maLMMO1rNcWONd4rmUtoyLZNA; csm-hit=tb:TPQ5S90PRKQPCMB4AN73+s-JM84HHTTHM0WP8VAC4JW|1563874732721&adb:adblk_no&t:1563874732721; x-main=\"ksoGkntwApSdV7?SsB2A@CxA5Pr@jijElZfZMOmTZ5o8ph1tn0UuRwksgxdxGR4Q\"");
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            log.warn("请求出错" + e);
            return null;
        }
    }

    public static String post(String url) {
        return post(url, null);
    }

    public static String get(String url, Map<String, String> params) {
        if (null != params) {
            StringBuilder stringBuilder = new StringBuilder(url);
            boolean flag = false;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                if (!flag) {
                    stringBuilder.append("?");
                    flag = true;
                } else {
                    stringBuilder.append("&");
                }
                stringBuilder.append(key).append("=").append(value);
            }
            url = stringBuilder.toString();
        }
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(2000).setConnectTimeout(2000).setSocketTimeout(2000).build()).build();
        try {
            HttpGet get = new HttpGet(url);
            get.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            get.setHeader("Accept", "Accept: text/plain, */*");
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
            get.addHeader("x-amazon-user-agent", "AmazonJavascriptScratchpad/1.0 (Language=Javascript)");
            get.addHeader("X-Requested-With", "XMLHttpRequest");
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                return null;
            }
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            log.warn("请求出错" + e);
            return null;
        }
    }

    public static String get(String url) {
        return get(url, null);
    }

    /**
     * appActionToken: HdafNEj2FzBWzslbZ7asI9Qv4f7dAj3D
     * appAction: SIGNIN
     * openid.return_to: ape:aHR0cHM6Ly9zZWxsZXJjZW50cmFsLmFtYXpvbi5jb20vaG9tZQ==
     * prevRID: ape:VDVZQTVaMVcwRUFURERRWUZINUQ=
     * workflowState: eyJ6aXAiOiJERUYiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiQTI1NktXIn0.mr2UA8lKufI5J-aKao3113pTdtkrPqS4vfGQTuGa7gA5h29gXGDZFw.v16nGVV1cklvJOLN.xJfZo0mp3_n_DMjH82brMK69Hq7KZWU-Y2k56K3-sS-y2enNklsxZqMsZZ6ni4nd2Nzw3sHyV1C4TVdaTCabbNLiMTux8ZzO-Z5uIegCGlkacT8NMKABXxh1Y9x-wTGu3DpVm2FHdDsW4oInYCjD-NV1WeklfjL-4vlWyyXB1FPo4ySyuM40NPBT-v44LXTnoXzkWT30q9pmMWjd2Uy3vGaC9k4W5HJMAu2aIHpz7-5zqElkcddfmTFH4EoxrsIx6m-OVaWIM9_LGkPn_2IRwb-vnRM-mNEXWXOziMcQq636P_PruG2dr99d0Ko65iI05SqA_hgr2gCIVGE5H5YaE8uemKj49o_srQcRWAZvr3OH_zqiuqFPf48G_us7PMTgoPuEZPNTn_koOTDYIk5vwoUjd7bWDak91CJoA60fE7e14NZGkGzhRW6lsAlgA7gCb5XcS_7XKaUtFDmsAUmctNXwkoFDGltMCnwPFv1RtpQvJraXLoHMjJaeh6rXLNewIgDHoI8zQIXksWeON3ewAp6Vne-brawiZUqlV1Ya9YBKcmjZzUu59OsKPgyFaMl3Qkrbq66kEeWObgaZyqD-yzgv69bmUseteikJqQwmApJOmRI2P8ZLfbeXJv2frmsatk-uubgUSMxsSuQC3wx4cfHwYYuM8b1OVk3MQvDe2nMhJt8Pz6T0RFwbGASmE2jrNIUh1gQPrD3iKdYnZXENoFthtAjxZwUWxnRPYyfgmLTe9H1kQcMtFV8SXb6vMqaTkTRB8gZ9-VHszxXAMRUcJv96Nroej7iWqGIsAe_Xmgcsk2PE-1UsjEOpjhndvBFDcv2amw.W8kN-yAoD3rMn7cuvAur2Q
     * email: chengguo@yesteria.club
     * create: 0
     * password: maiyang123
     * <p>
     * reportEndDate: 1563874722169
     * reportName: "商品推广自动投放报告"
     * reportPeriod: "LAST_MONTH"
     * reportProgramType: "sp"
     * reportStartDate: 1563269922169
     * reportType: "SEARCH_TERMS_REPORT"
     * reportUnit: "summary"
     * <p>
     * <p>
     * <p>
     * sEcho: 1
     * parentCreationDate: 0
     * iDisplayStart: 0
     * iDisplayLength: 50
     * statisticsPeriod: LIFETIME
     * aggregates: false
     * filters: {"programType":{"eq":["unified"]}}
     *
     * @param args
     */
    public static void main(String[] args) {
       /* String url = "https://sellercentral.amazon.com/sspa/tresah/create";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("reportEndDate", "1563874722169"));
        params.add(new BasicNameValuePair("reportName", "商品推广自动投放报告"));
        params.add(new BasicNameValuePair("reportPeriod", "LAST_MONTH"));
        params.add(new BasicNameValuePair("reportProgramType", "sp"));
        params.add(new BasicNameValuePair("reportType", "SEARCH_TERMS_REPORT"));
        params.add(new BasicNameValuePair("reportUnit", "summary"));
        String post = post(url, params);
        log.warn(post);*/

        String url = "https://sellercentral.amazon.com/sspa/tresah/reports";
        Map<String, String> params = new HashMap<>();
        params.put("sEcho", "1");
        params.put("parentCreationDate", "0");
        params.put("iDisplayStart", "0");
        params.put("iDisplayLength", "50");
        params.put("statisticsPeriod", "LIFETIME");
        String get = get(url, params);
        log.warn(get);
    }

}
