-- =========================
-- V1__init.sql
-- База для e‑commerce: роли, пользователи, бренды, категории, товары, заказы и т.д.
-- Цены/суммы храним в "целых копейках/тиынах" (INTEGER), 1999 = 19.99
-- =========================

-- Роли и пользователи
create table roles (
                       id          bigserial primary key,
                       name        varchar(50) not null unique
);

create table users (
                       id           bigserial primary key,
                       email        varchar(255) not null unique,
                       password_hash varchar(255) not null,
                       full_name    varchar(255) not null,
                       phone        varchar(20),
                       is_active    boolean not null default true,
                       created_at   timestamp not null default current_timestamp,
                       updated_at   timestamp not null default current_timestamp
);

create table user_roles (
                            user_id  bigint not null references users(id) on delete cascade,
                            role_id  bigint not null references roles(id) on delete cascade,
                            primary key (user_id, role_id)
);

-- Базовые роли
insert into roles(name) values ('ROLE_CUSTOMER'), ('ROLE_ADMIN'), ('ROLE_MANAGER');

-- Бренды и категории (категории — иерархия через parent_id)
create table brands (
                        id        bigserial primary key,
                        name      varchar(255) not null unique,
                        slug      varchar(255) not null unique
);

create table categories (
                            id         bigserial primary key,
                            name       varchar(255) not null,
                            slug       varchar(255) not null unique,
                            parent_id  bigint references categories(id) on delete set null,
                            created_at timestamp not null default current_timestamp,
                            updated_at timestamp not null default current_timestamp
);

create index idx_categories_parent on categories(parent_id);

-- Товары (привязка к бренду + many-to-many к категориям)
create table products (
                          id                       bigserial primary key,
                          name                     varchar(255) not null,
                          article                  varchar(100) not null unique, -- артикул/SKU
                          price                    integer not null check (price >= 0),
                          photo                    text,
                          description              text,
                          technical_characteristics text,
                          stock                    integer not null default 0 check (stock >= 0),
                          brand_id                 bigint references brands(id) on delete set null,
                          rating                   double precision not null default 0.0,
                          rating_count             integer not null default 0,
                          created_at               timestamp not null default current_timestamp,
                          updated_at               timestamp not null default current_timestamp
);

create index idx_products_brand on products(brand_id);

-- Связь продукт ↔ категория (многие-ко-многим)
create table product_categories (
                                    product_id  bigint not null references products(id) on delete cascade,
                                    category_id bigint not null references categories(id) on delete cascade,
                                    primary key (product_id, category_id)
);

-- Заказы и состав заказов
create table orders (
                        id           bigserial primary key,
                        user_id      bigint references users(id) on delete cascade,
                        contact_name   varchar(255),
                        contact_phone  varchar(20),
                        contact_email  varchar(255),
                        payment_method varchar(50),
                        customer_note  text,
                        total_price  integer not null check (total_price >= 0),
                        status       varchar(50) not null default 'PENDING', -- PENDING / PAID / SHIPPED / COMPLETED / CANCELLED
                        created_at   timestamp not null default current_timestamp,
                        updated_at   timestamp not null default current_timestamp,
                        constraint chk_orders_status check (status in ('PENDING','PAID','SHIPPED','COMPLETED','CANCELLED'))
);

create index idx_orders_user on orders(user_id);

create table order_items (
                             id          bigserial primary key,
                             order_id    bigint not null references orders(id) on delete cascade,
                             product_id  bigint not null references products(id) on delete restrict,
                             price       integer not null check (price >= 0), -- цена на момент покупки (фиксируется)
                             quantity    integer not null check (quantity > 0)
);

create index idx_order_items_order on order_items(order_id);
create index idx_order_items_product on order_items(product_id);

create table order_status_history (
                                      id         bigserial primary key,
                                      order_id   bigint not null references orders(id) on delete cascade,
                                      old_status varchar(50),
                                      new_status varchar(50) not null,
                                      changed_at timestamp not null default current_timestamp,
                                      constraint chk_osh_new_status check (new_status in ('PENDING','PAID','SHIPPED','COMPLETED','CANCELLED'))
);

create index idx_osh_order on order_status_history(order_id);

-- Платежи
create table payments (
                          id              bigserial primary key,
                          order_id        bigint not null references orders(id) on delete cascade,
                          amount          integer not null check (amount >= 0),
                          payment_method  varchar(50) not null,   -- CARD / CASH / TRANSFER / ...
                          payment_status  varchar(50) not null,   -- PENDING / SUCCESS / FAILED / REFUNDED
                          transaction_id  varchar(255),
                          created_at      timestamp not null default current_timestamp,
                          constraint chk_payment_status check (payment_status in ('PENDING','SUCCESS','FAILED','REFUNDED'))
);

create index idx_payments_order on payments(order_id);

-- Доставка
create table shipping (
                          id               bigserial primary key,
                          order_id         bigint not null references orders(id) on delete cascade,
                          address          text not null,
                          city             varchar(100) not null,
                          postal_code      varchar(20) not null,
                          country          varchar(100) not null,
                          shipping_method  varchar(50) not null,
                          tracking_number  varchar(255),
                          shipping_status  varchar(50) not null default 'PENDING', -- PENDING / IN_TRANSIT / DELIVERED / RETURNED
                          shipped_at       timestamp,
                          delivered_at     timestamp,
                          constraint chk_shipping_status check (shipping_status in ('PENDING','IN_TRANSIT','DELIVERED','RETURNED'))
);

create index idx_shipping_order on shipping(order_id);

-- Купоны и связь заказ ↔ купоны
create table coupons (
                         id               bigserial primary key,
                         code             varchar(50) not null unique,
                         discount_percent integer check (discount_percent >= 0 and discount_percent <= 100),
                         discount_amount  integer check (discount_amount >= 0),
                         valid_from       timestamp,
                         valid_to         timestamp,
                         usage_limit      integer check (usage_limit >= 0),
                         constraint chk_coupon_dates check (valid_from is null or valid_to is null or valid_from <= valid_to),
    -- Ровно одно из discount_percent / discount_amount должно быть задано
                         constraint chk_coupon_one_kind check (
                             (discount_percent is not null and discount_amount is null)
                                 or (discount_percent is null and discount_amount is not null)
                             )
);

create table order_coupons (
                               id         bigserial primary key,
                               order_id   bigint not null references orders(id) on delete cascade,
                               coupon_id  bigint not null references coupons(id) on delete cascade
);

create index idx_order_coupons_order on order_coupons(order_id);
create index idx_order_coupons_coupon on order_coupons(coupon_id);

-- 8) Отзывы
create table reviews (
                         id         bigserial primary key,
                         product_id bigint not null references products(id) on delete cascade,
                         user_id    bigint not null references users(id) on delete cascade,
                         rating     integer not null check (rating between 1 and 5),
                         comment    text,
                         created_at timestamp not null default current_timestamp,
                         constraint uq_review_once unique (product_id, user_id)
);

create index idx_reviews_product on reviews(product_id);
create index idx_reviews_user on reviews(user_id);

-- Функция и триггеры для автообновления updated_at (PostgreSQL не поддерживает ON UPDATE по умолчанию)
create or replace function set_updated_at()
returns trigger as $$
begin
  new.updated_at = current_timestamp;
return new;
end;
$$ language plpgsql;

-- навешиваем на таблицы, где есть updated_at
create trigger trg_users_updated
    before update on users
    for each row execute function set_updated_at();

create trigger trg_categories_updated
    before update on categories
    for each row execute function set_updated_at();

create trigger trg_products_updated
    before update on products
    for each row execute function set_updated_at();

create trigger trg_orders_updated
    before update on orders
    for each row execute function set_updated_at();
