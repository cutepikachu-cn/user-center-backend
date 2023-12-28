drop database if exists user_center;
create database user_center default collate utf8mb4_unicode_ci default charset utf8mb4;
use user_center;

drop table if exists user;
create table if not exists user
(
    id          bigint(20) auto_increment comment 'id',
    account     varchar(256) not null comment '账户',
    nickname    varchar(256) comment '昵称',
    avatar_url  varchar(1024) comment '头像url',
    profile     varchar(512) comment '个人简介',
    gender      bit          not null default 0 comment '性别',
    age         int          not null default 1 comment '年龄',
    password    varchar(256) not null comment '密码',
    phone       varchar(256) comment '手机号',
    email       varchar(256) comment '邮箱',
    status      tinyint      not null default 0 comment '是否有效（0：正常）',
    create_time datetime     not null default current_timestamp comment '创建时间',
    update_time timestamp    not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   bit          not null default 0 comment '是否删除',
    role        tinyint      not null default 0 comment '用户角色（0：普通用户，1：管理员）',
    tags        varchar(512) comment '用户标签列表，以`,`分割',
    constraint pk_id primary key (id),
    constraint uk_account unique key (account)
) collate utf8mb4_unicode_ci
  charset utf8mb4 comment '用户表';

drop table if exists tag;
create table if not exists tag
(
    id           int auto_increment comment 'id',
    tag_name     varchar(256) not null comment '标签名',
    user_id      bigint(20)   not null comment '添加该标签的用户id',
    parent_id    int comment '父标签id',
    has_children bit          not null default 0 comment '是否有子标签',
    create_time  datetime     not null default current_timestamp comment '创建时间',
    update_time  timestamp    not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete    bit          not null default 0 comment '是否删除',
    constraint pk_id primary key (id),
    constraint uk_tag_name unique key (tag_name),
    index idx_user_id (user_id)
) collate utf8mb4_unicode_ci
  charset utf8mb4 comment '标签表';

drop table if exists team;
create table team
(
    id          bigint auto_increment comment 'id',
    name        varchar(64) not null comment '队伍名称',
    description varchar(512) comment '队伍描述',
    max_number  int         not null default 2 comment '队伍最大人数',
    expire_time datetime    not null comment '过期时间',
    user_id     bigint      not null comment '队长id',
    status      int         not null default 0 comment '队伍状态（默认0 ~ 公开；1 ~ 私有；2 ~ 加密）',
    password    varchar(256) comment '队伍密码（加密队伍需密码加入）',
    tags        varchar(128) comment '用户标签列表，以`,`分割',
    create_time datetime    not null default current_timestamp comment '创建时间',
    update_time timestamp   not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   bit         not null default 0 comment '是否删除',
    constraint pk_id primary key (id)
) collate utf8mb4_unicode_ci
  charset utf8mb4 comment '队伍表';

drop table if exists user_team;
create table user_team
(
    id          bigint auto_increment comment 'id',
    user_id     bigint    not null comment '用户id',
    team_id     bigint    not null comment '队伍id',
    join_time   datetime  null     default current_timestamp comment '加入时间',
    create_time datetime  not null default current_timestamp comment '创建时间',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   bit       not null default 0 comment '是否删除',
    constraint pk_id primary key (id),
    constraint uk_user_team unique (user_id, team_id)
) collate utf8mb4_unicode_ci
  charset utf8mb4 comment '队伍~用户关系表';
