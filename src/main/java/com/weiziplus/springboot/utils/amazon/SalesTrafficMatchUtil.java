package com.weiziplus.springboot.utils.amazon;

import com.weiziplus.springboot.models.DetailPageSalesAndTraffic;
import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByChildItems;
import com.weiziplus.springboot.models.DetailPageSalesAndTrafficByParentItems;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class SalesTrafficMatchUtil {

    public static DetailPageSalesAndTraffic columnMatch(List<String> columList, List<String> dataList) {
        DetailPageSalesAndTraffic detailPageSalesAndTraffic = new DetailPageSalesAndTraffic();
        for (int i = 0; i < columList.size(); i++) {
            if ("(Parent) ASIN".equals(columList.get(i))) {
                detailPageSalesAndTraffic.setParentAsin(dataList.get(i));
                continue;
            }
            if ("(Child) ASIN".equals(columList.get(i))) {
                detailPageSalesAndTraffic.setChildAsin(dataList.get(i));
                continue;
            }
            if ("Title".equals(columList.get(i))) {
                detailPageSalesAndTraffic.setProductName(dataList.get(i));
                continue;
            }
            if ("SKU".equals(columList.get(i))) {
                detailPageSalesAndTraffic.setSku(dataList.get(i));
                continue;
            }
            if ("Sessions".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setBuyerVisits(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setBuyerVisits(Integer.valueOf(str));
                }
                continue;
            }
            if ("Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setBuyerVisitsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal buyerVisitsPercentage = new BigDecimal(str);
                    detailPageSalesAndTraffic.setBuyerVisitsPercentage(buyerVisitsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Page Views".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setPageViews(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setPageViews(Integer.valueOf(str));
                }
                continue;
            }
            if ("Page Views Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setPageViewsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal pageViewsPercentage = new BigDecimal(str);
                    detailPageSalesAndTraffic.setPageViewsPercentage(pageViewsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Buy Box Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setPurchaseButtonWinRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal purchaseButtonWinRate = new BigDecimal(str);
                    detailPageSalesAndTraffic.setPurchaseButtonWinRate(purchaseButtonWinRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Units Ordered".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderedItemsNumber(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setOrderedItemsNumber(Integer.valueOf(str));
                }
                continue;
            }
            if ("Units Ordered - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderQuantityB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setOrderQuantityB2b(Integer.valueOf(str));
                }
                continue;
            }
            if ("Unit Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderItemsConversionRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal orderItemsConversionRate = new BigDecimal(str);
                    detailPageSalesAndTraffic.setOrderItemsConversionRate(orderItemsConversionRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Unit Session Percentage - B2B".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setItemsConversionRateB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal itemsConversionRateB2b = new BigDecimal(str);
                    detailPageSalesAndTraffic.setItemsConversionRateB2b(itemsConversionRateB2b.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Ordered Product Sales".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderedItemSales(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTraffic.setOrderedItemSales(new BigDecimal(str));
                }
                continue;
            }
            if ("Ordered Product Sales - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderedItemSalesB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTraffic.setOrderedItemSalesB2b(new BigDecimal(str));
                }
                continue;
            }
            if ("Total Order Items".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setOrderProductCategorys(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setOrderProductCategorys(Integer.valueOf(str));
                }
                continue;
            }
            if ("Total Order Items - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTraffic.setTotalOrderItemsB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTraffic.setTotalOrderItemsB2b(Integer.valueOf(str));
                }
                continue;
            }
        }
        return detailPageSalesAndTraffic;
    }

    public static DetailPageSalesAndTrafficByParentItems columnMatchByParentItems(List<String> columList, List<String> dataList) {
        DetailPageSalesAndTrafficByParentItems detailPageSalesAndTrafficByParentItems = new DetailPageSalesAndTrafficByParentItems();
        for (int i = 0; i < columList.size(); i++) {
            if ("(Parent) ASIN".equals(columList.get(i))) {
                detailPageSalesAndTrafficByParentItems.setParentAsin(dataList.get(i));
                continue;
            }
            if ("Title".equals(columList.get(i))) {
                detailPageSalesAndTrafficByParentItems.setProductName(dataList.get(i));
                continue;
            }
            if ("Sessions".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setBuyerVisits(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setBuyerVisits(Integer.valueOf(str));
                }
                continue;
            }
            if ("Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setBuyerVisitsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal buyerVisitsPercentage = new BigDecimal(str);
                    detailPageSalesAndTrafficByParentItems.setBuyerVisitsPercentage(buyerVisitsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Page Views".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setPageViews(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setPageViews(Integer.valueOf(str));
                }

                continue;
            }
            if ("Page Views Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setPageViewsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal pageViewsPercentage = new BigDecimal(str);
                    detailPageSalesAndTrafficByParentItems.setPageViewsPercentage(pageViewsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Buy Box Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setPurchaseButtonWinRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal purchaseButtonWinRate = new BigDecimal(str);
                    detailPageSalesAndTrafficByParentItems.setPurchaseButtonWinRate(purchaseButtonWinRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Units Ordered".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderedItemsNumber(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setOrderedItemsNumber(Integer.valueOf(str));
                }
                continue;
            }
            if ("Units Ordered - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderQuantityB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setOrderQuantityB2b(Integer.valueOf(str));
                }

                continue;
            }
            if ("Unit Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderItemsConversionRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal orderItemsConversionRate = new BigDecimal(str);
                    detailPageSalesAndTrafficByParentItems.setOrderItemsConversionRate(orderItemsConversionRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Unit Session Percentage - B2B".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setItemsConversionRateB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal itemsConversionRateB2b = new BigDecimal(str);
                    detailPageSalesAndTrafficByParentItems.setItemsConversionRateB2b(itemsConversionRateB2b.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Ordered Product Sales".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderedItemSales(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTrafficByParentItems.setOrderedItemSales(new BigDecimal(str));
                }
                continue;
            }
            if ("Ordered Product Sales - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderedItemSalesB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTrafficByParentItems.setOrderedItemSalesB2b(new BigDecimal(str));
                }
                continue;
            }
            if ("Total Order Items".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setOrderProductCategorys(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setOrderProductCategorys(Integer.valueOf(str));
                }
                continue;
            }
            if ("Total Order Items - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByParentItems.setTotalOrderItemsB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByParentItems.setTotalOrderItemsB2b(Integer.valueOf(str));
                }
                continue;
            }
        }
        return detailPageSalesAndTrafficByParentItems;
    }

    public static DetailPageSalesAndTrafficByChildItems columnMatchByChildItems(List<String> columList, List<String> dataList) {
        DetailPageSalesAndTrafficByChildItems detailPageSalesAndTrafficByChildItems = new DetailPageSalesAndTrafficByChildItems();
        for (int i = 0; i < columList.size(); i++) {
            if ("(Parent) ASIN".equals(columList.get(i))) {
                detailPageSalesAndTrafficByChildItems.setParentAsin(dataList.get(i));
                continue;
            }
            if ("(Child) ASIN".equals(columList.get(i))) {
                detailPageSalesAndTrafficByChildItems.setChildAsin(dataList.get(i));
                continue;
            }
            if ("Title".equals(columList.get(i))) {
                detailPageSalesAndTrafficByChildItems.setProductName(dataList.get(i));
                continue;
            }
            if ("Sessions".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setBuyerVisits(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setBuyerVisits(Integer.valueOf(str));
                }
                continue;
            }
            if ("Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setBuyerVisitsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal buyerVisitsPercentage = new BigDecimal(str);
                    detailPageSalesAndTrafficByChildItems.setBuyerVisitsPercentage(buyerVisitsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Page Views".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setPageViews(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setPageViews(Integer.valueOf(str));
                }
                continue;
            }
            if ("Page Views Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setPageViewsPercentage(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal pageViewsPercentage = new BigDecimal(str);
                    detailPageSalesAndTrafficByChildItems.setPageViewsPercentage(pageViewsPercentage.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Buy Box Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setPurchaseButtonWinRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal purchaseButtonWinRate = new BigDecimal(str);
                    detailPageSalesAndTrafficByChildItems.setPurchaseButtonWinRate(purchaseButtonWinRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Units Ordered".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderedItemsNumber(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setOrderedItemsNumber(Integer.valueOf(str));
                }
                continue;
            }
            if ("Units Ordered - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderQuantityB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setOrderQuantityB2b(Integer.valueOf(str));
                }
                continue;
            }
            if ("Unit Session Percentage".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderItemsConversionRate(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal orderItemsConversionRate = new BigDecimal(str);
                    detailPageSalesAndTrafficByChildItems.setOrderItemsConversionRate(orderItemsConversionRate.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Unit Session Percentage - B2B".equals(columList.get(i))) {
                //将百分数的字符串转换成bigdecimal
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setItemsConversionRateB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replace("%", "");
                    str = str.replace(",","");
                    BigDecimal itemsConversionRateB2b = new BigDecimal(str);
                    detailPageSalesAndTrafficByChildItems.setItemsConversionRateB2b(itemsConversionRateB2b.divide(new BigDecimal(100)));
                }
                continue;
            }
            if ("Ordered Product Sales".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderedItemSales(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTrafficByChildItems.setOrderedItemSales(new BigDecimal(str));
                }
                continue;
            }
            if ("Ordered Product Sales - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderedItemSalesB2b(new BigDecimal(0));
                } else {
                    String str = dataList.get(i).replaceAll("[A-Za-z$£€￥,]", "");
                    detailPageSalesAndTrafficByChildItems.setOrderedItemSalesB2b(new BigDecimal(str));
                }
                continue;
            }
            if ("Total Order Items".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setOrderProductCategorys(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setOrderProductCategorys(Integer.valueOf(str));
                }
                continue;
            }
            if ("Total Order Items - B2B".equals(columList.get(i))) {
                if (null == dataList.get(i) || "".equals(dataList.get(i))) {
                    detailPageSalesAndTrafficByChildItems.setTotalOrderItemsB2b(Integer.valueOf(0));
                } else {
                    String str = dataList.get(i).replace(",", "");
                    detailPageSalesAndTrafficByChildItems.setTotalOrderItemsB2b(Integer.valueOf(str));
                }
                continue;
            }
        }
        return detailPageSalesAndTrafficByChildItems;
    }
}
