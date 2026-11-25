# jdk1.8 升级到 jdk11 遇到的一些坑

## 一、jdk11 移除了 Base64

替代方案

```
        Base64.getEncoder().encodeToString

        Base64.getDecoder().decode
```

## 二、Maven 打包报错

修改方案,更新maven打包插件

```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<version>3.8.0</version>
		<configuration>
			<release>11</release>
			<encoding>UTF-8</encoding>
		</configuration>
</plugin>
```

## 三、Lombok 无法使用

解决方案，升级lombok

```

        <dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.4</version>
			<scope>provided</scope>
		</dependency>
```

## 四、xmlelement 注解无法使用

解决方案，引入外部jar，jdk11已经移除此包

```
<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-core</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-impl</artifactId>
			<version>2.3.0</version>
		</dependency>
```
