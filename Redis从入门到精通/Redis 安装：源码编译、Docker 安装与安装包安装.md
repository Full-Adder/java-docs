# Redis 安装：源码编译、Docker 安装与安装包安装

Redis 是一个开源的内存中数据结构存储系统，用作数据库、缓存和消息代理。它可以从源代码编译安装，也可以通过 Docker 或特定平台的安装包进行安装。本文档将详细介绍这三种安装方法。

## 源码编译安装

### 环境要求
- 操作系统：Linux/Unix/MacOS
- 编译工具：gcc、make
- 库依赖：jemalloc（可选，但推荐）

### 安装步骤

1. **安装依赖**（以 Ubuntu 为例）:

    ```bash
    sudo apt-get update
    sudo apt-get install build-essential libjemalloc-dev
    apt install make
    apt install make-guile
    apt-get install pkg-config
    apt install -y gcc
    
    ```

2. **下载 Redis 源码**:

    ```bash
    wget http://download.redis.io/redis-stable.tar.gz
    tar xzf redis-stable.tar.gz
    cd redis-stable
    ```

3. **编译 Redis**:

    ```bash
    make distclean # 第一次运行不需要
    make MALLOC=libc
    ```

4. **运行测试**（可选，但推荐）:

    ```bash
    make test
    ```

5. **安装 Redis**:
    这个是将一些列文件注册到bin中，方便在任何地方都能使用
    ```bash
    sudo make install
    ```

6. **启动 Redis 服务**:

    ```bash
    sudo redis-server
    ```

## Docker 安装

### 环境要求
- Docker 环境

### 安装步骤

1. **拉取 Redis Docker 镜像**:

    ```bash
    docker pull redis
    ```

2. **运行 Redis 容器**:

    ```bash
    docker run --name my-redis -p 6379:6379 -d redis
    ```

    这将在后台启动一个名为 `my-redis` 的 Redis 容器，并将容器的 6379 端口映射到宿主机的 6379 端口。

3. **验证 Redis 容器状态**:

    ```bash
    docker ps
    ```

4. **进入 Redis 容器**:

    ```bash
    docker exec -it my-redis /bin/bash
    ```

5. **使用 redis-cli 连接 Redis 容器**:

    ```bash
    redis-cli
    ```

## 安装包安装

### Linux 下的安装

#### Ubuntu/Debian

1. **添加 Redis 仓库**:

    ```bash
    wget -qO - https://packages.redis.io/redis-stable/pubkey.gpg | sudo apt-key add -
    sudo add-apt-repository 'deb https://packages.redis.io/redis-stable $(lsb_release -cs) main'
    sudo apt-get update
    ```

2. **安装 Redis**:

    ```bash
    sudo apt-get install redis-server
    ```

3. **启动 Redis 服务**:

    ```bash
    sudo systemctl start redis-server
    ```

#### CentOS/RHEL

1. **安装 EPEL 仓库**:

    ```bash
    sudo yum install epel-release
    ```

2. **安装 Redis**:

    ```bash
    sudo yum install redis
    ```

3. **启动 Redis 服务**:

    ```bash
    sudo systemctl start redis
    ```

### MacOS 下的安装

1. **安装 Homebrew**（如果尚未安装）:

    ```bash
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    ```

2. **使用 Homebrew 安装 Redis**:

    ```bash
    brew install redis
    ```

3. **启动 Redis 服务**:

    ```bash
    brew services start redis
    ```

## 验证安装

无论哪种安装方法，都可以通过以下命令来验证 Redis 是否安装成功：

1. **检查 Redis 服务状态**:

    ```bash
    redis-cli ping
    ```

    如果返回 `PONG`，则表示 Redis 服务正在运行。

2. **查看 Redis 版本**:

    ```bash
    redis-cli --version
    ```

## 总结

Redis 提供了多种安装方式，以满足不同用户的需求。源码编译安装允许用户获取最新特性并自定义配置，Docker 安装提供了快速部署和隔离环境的优势，而安装包安装则简化了安装流程，适合大多数用户。选择适合您需求的安装方式，开始体验 Redis 的强大功能吧。