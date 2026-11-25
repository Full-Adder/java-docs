# 别和我说你还不会使用lambda表达式

最近在项目上面经常使用lambda表达式，但是总是记不住，一直都在百度，写完之后就忘记了，感觉很费时间；这次就花点时间，把一些常用的lambada 处理集合的实例都保存了下来(去重，分组，求和，list转map等等)，以后就不用到处找了，刚好也可以给同学们分享下；另外也把一些关于使用lambda时遇到的坑也给大家一起分享下，所有代码拿来即用！！！本文档持续更新...

## 实例演示

商品实体

```
@Data
@AllArgsConstructor
public class GoodInfo {
    private String mallSource;
    private String skuNo;
    private int price;
    private int monthCount;
}
```

### 排序

集合排序在项目中用的频率还蛮高，这里以按照销量排序为例

```
   List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 按照销量正序 从小到大排序
    goodInfos.sort(Comparator.comparing(GoodInfo::getMonthCount));
    // 按照销量倒序 从大到小排序
    goodInfos.sort(Comparator.comparing(GoodInfo::getMonthCount).reversed());
```

### 取最大值/取最小值/求和

```
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 获取最大销量  注意如果求最大值是在filter之后使用例如，goodInfos.stream().filter().max一定要判断filter后集合数量是否不为空，否则使用max的get方法会报错
    GoodInfo hotGoodInfo = goodInfos.stream().max(Comparator.comparing(GoodInfo::getMonthCount)).get();
    // 求最低价格商品
    GoodInfo lowPriceGoodInfo = goodInfos.stream().min(Comparator.comparing(GoodInfo::getMonthCount)).get();
    // 计算商品总价格
    int sum = goodInfos.stream().mapToInt(person -> person.getPrice()).sum();
    // 求平均价格
    double avg = goodInfos.stream().mapToInt(person -> person.getPrice()).average().getAsDouble();
```

### 遍历

```
   List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 遍历输出所有商品id
    goodInfos.forEach(
        goodInfo -> {
          System.out.println(goodInfo.getSkuNo());
        });
```

### 实体集合 转 单个属性的集合

往往在我们项目中会有这样的需求：我需要提取集合中某一个属性，然后组装成集合，通常做法是先创建一个字符串集合，然后遍历原始集合，取出数据，放到字符串集合中，虽然也能实现功能，但是不免太过于繁琐，现在使用一行lambada表达式即可搞定：

```
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 将list转为某个属性map 这里是把所有skuno全部取出来 作为集合
    List<String> skuNos = goodInfos.stream().map(goodInfo -> goodInfo.getSkuNo()).collect(Collectors.toList());
```

### 实体集合 转 map 返回

```
    List<GoodInfo> goodInfos = Arrays.asList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 将list转为map，key 为商品id
    Map<String, GoodInfo> map = goodInfos.stream().collect(Collectors.toMap(GoodInfo::getSkuNo, goodInfo -> goodInfo));
```

### 实体集合按照某个属性分组

```
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 按照商品来源分组
    Map<String, List<GoodInfo>> map = goodInfos.stream().collect(Collectors.groupingBy(GoodInfo::getMallSource));
```

### 过滤数据（记得接收）

```
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 过滤商品价格大于300的
    // todo 过滤后一定要使用集合接收，否则等于没有过滤

    List<GoodInfo> collect =
        goodInfos.stream()
            .filter(goodInfo -> goodInfo.getPrice() > 300)
            .collect(Collectors.toList());
    collect.forEach(
        goodInfo -> {
          System.out.println(goodInfo.getPrice());
        });
```

### 去重(两种方法可选)

#### 方法一 set 去重

```
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 使用treeset 集合来实现去重，这里一定要使用集合接收，不然等于没有去重
    List<GoodInfo> goodInfos1 =
        goodInfos.stream()
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(o -> o.getSkuNo()))),
                    ArrayList::new));
```

#### 方法二 map 去重

```
public static void main(String[] args) {
    List<GoodInfo> goodInfos = new ArrayList();
    goodInfos.add(new GoodInfo("tb", "tb_1112312312", 199, 100000));
    goodInfos.add(new GoodInfo("tb", "tb_23534231231", 399, 10));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_1110080098", 299, 100));
    goodInfos.add(new GoodInfo("jd", "jd_412313123", 99, 10000000));
    goodInfos.add(new GoodInfo("pdd", "pdd_354532431", 599, 1));
    goodInfos.add(new GoodInfo("pdd", "pdd_1423124131", 499, 10));

    // 使用map去重
    List<GoodInfo> goodInfos2 =
        goodInfos.stream()
            .filter(distinctByKey(goodInfo -> goodInfo.getSkuNo()))
            .collect(Collectors.toList());
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }
```

## 遇到的坑

### 坑一

-  报错信息：
java.util.NoSuchElementException: No value present 
-  解决方案：
一般出现这个错都是在filter操作后面使用了max/min等操作，然后调用了get方法，取不到数据，导致报错，所以建议检查filter操作后时候还有数据，有数据再进行后续操作： 

```
    Optional<User> optional = goodInfos.stream().max(userComparator);
if(optional != null && optional.isPresent()) {
    recentUserServer = optional.get().getServer();
}
```

### 坑二

- 使用了filter为什么没起作用呢？

调用filter之后，它是有返回值的，所以你需要使用新的集合去接收

。。。

后续慢慢填坑
