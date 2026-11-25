# Redis GEO: 地理空间索引与距离计算


Redis 是一个功能强大的内存数据库，它不仅提供了丰富的数据结构，还支持地理空间数据的存储和查询。从 3.2 版本开始，Redis 引入了对地理空间（Geospatial）数据的支持，这使得 Redis 成为了处理基于位置的服务（Location-Based Services, LBS）的理想选择。

## 地理空间数据简介

地理空间数据是指与地理位置相关的数据，例如经纬度坐标、地点名称、边界等。在许多应用中，如地图服务、导航系统、位置共享等，地理空间数据扮演着重要的角色。

## Redis GEO 的特性

1. **存储地理位置**：Redis 可以存储地理位置信息，每个位置由一个经纬度坐标和一个关联的名称组成。
2. **计算距离**：Redis 可以计算两个地理位置之间的距离。
3. **范围查询**：Redis 支持查找指定范围内的地理位置。
4. **排序**：可以根据距离或其他标准对地理位置进行排序。
5. **数据结构**：Redis 使用有序集合（Sorted Sets）来存储地理空间数据。

## Redis GEO 的命令

Redis 提供了以下地理空间相关的命令：

- `GEOADD key longitude latitude member [longitude latitude member ...]`：向键中添加地理位置。
- `GEODIST key member1 member2 [unit]`：返回两个地理位置之间的距离。
- `GEOHASH key member [member ...]`：返回一个或多个地理位置的 Geohash。
- `GEOPOS key member [member ...]`：返回一个或多个地理位置的坐标。
- `GEORADIUS key longitude latitude radius unit [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]`：查询指定范围内的地理位置。
- `GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]`


## 应用场景

1. **地图服务**：用于存储和查询地图上的位置信息。
2. **位置共享**：在社交网络或旅行应用中，用于共享和查看朋友的位置。
3. **导航系统**：辅助实现路径规划和导航功能。
4. **本地搜索**：根据用户的位置提供相关的搜索结果。

## 案例分析
```
1. **合肥（Hefei）**
   - 经度：117.22°E
   - 纬度：31.86°N

2. **芜湖（Wuhu）**
   - 经度：118.38°E
   - 纬度：31.33°N

3. **蚌埠（Bengbu）**
   - 经度：117.36°E
   - 纬度：32.91°N

4. **淮南（Huainan）**
   - 经度：116.98°E
   - 纬度：32.63°N

5. **马鞍山（Ma'anshan）**
   - 经度：118.49°E
   - 纬度：31.70°N

6. **淮北（Huaibei）**
   - 经度：116.77°E
   - 纬度：33.97°N

7. **铜陵（Tongling）**
   - 经度：117.81°E
   - 纬度：30.93°N

8. **安庆（Anqing）**
   - 经度：117.05°E
   - 纬度：30.51°N

9. **黄山（Huangshan）**
   - 经度：118.34°E
   - 纬度：29.72°N

10. **滁州（Chuzhou）**
    - 经度：118.32°E
    - 纬度：32.31°N

11. **阜阳（Fuyang）**
    - 经度：115.82°E
    - 纬度：32.90°N

12. **宿州（Suzhou）**
    - 经度：116.98°E
    - 纬度：33.64°N

13. **六安（Lu'an）**
    - 经度：116.52°E
    - 纬度：31.73°N

14. **亳州（Bozhou）**
    - 经度：115.78°E
    - 纬度：33.85°N

15. **池州（Chizhou）**
    - 经度：117.49°E
    - 纬度：30.67°N

16. **宣城（Xuancheng）**
    - 经度：118.76°E
    - 纬度：30.95°N
```

```
# 填充数据
GEOADD anhui 117.22 31.86 "Hefei" 118.38 31.33 "Wuhu" 117.36 32.91 "Bengbu" 116.98 32.63 "Huainan" 118.49 31.70 "Ma'anshan" 116.77 33.97 "Huaibei" 117.81 30.93 "Tongling" 117.05 30.51 "Anqing" 118.34 29.72 "Huangshan" 118.32 32.31 "Chuzhou" 115.82 32.90 "Fuyang" 116.98 33.64 "Suzhou" 116.52 31.73 "Lu'an" 115.78 33.85 "Bozhou" 117.49 30.67 "Chizhou" 118.76 30.95 "Xuancheng"
```
假设我们正在开发一个旅行推荐应用，我想实现以下功能

* 我想查找合肥到芜湖的距离，使用KM为单位

```bash
127.0.0.1:6379> GEODIST anhui "Hefei" "Wuhu" km
"124.7091"
```
* 我现在位置（117.49 30.67），我想看下200KM以内有哪些城市

```bash
127.0.0.1:6379> GEORADIUS anhui 117.49 30.67 200 km
 1) "Anqing"
 2) "Lu'an"
 3) "Chizhou"
 4) "Tongling"
 5) "Hefei"
 6) "Huangshan"
 7) "Xuancheng"
 8) "Wuhu"
 9) "Ma'anshan"
10) "Chuzhou"
```
* 我现在在芜湖，我想看下200km有哪些城市

```
127.0.0.1:6379> georadiusbymember anhui "Wuhu" 200 km
 1) "Huangshan"
 2) "Xuancheng"
 3) "Wuhu"
 4) "Ma'anshan"
 5) "Chuzhou"
 6) "Anqing"
 7) "Lu'an"
 8) "Chizhou"
 9) "Tongling"
10) "Hefei"
```

## 总结

Redis 的 GEO 功能为处理地理空间数据提供了强大的支持。通过简单的命令，开发者可以在应用中轻松实现地理位置的存储、查询和计算。无论是地图服务、位置共享还是本地搜索，Redis GEO 都能提供高效、灵活的解决方案。随着基于位置服务的日益普及，Redis GEO 功能的应用前景将更加广阔。
