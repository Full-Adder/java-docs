# MySQL 哪些用法会导致索引失效

- 使用or关键字 （必须or前后的字段都建立了索引）
- 使用like关键字，除了 like ‘xxx%’ like '%sss%' 、 like '%dddd'
- 联合索引，没有联合索引顺序查询 如 索引：abc，查询 where b a c
- 索引列有运算符！= 或者 not in
- 索引列使用了函数 如：where =10
- 隐式类型转换。 int a  a = '20'
