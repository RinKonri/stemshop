-- Корзины и позиции корзины (гость: user_id IS NULL; залогиненный: user_id = users.id)
create table carts (
                       id             uuid primary key,
                       user_id        bigint references users(id) on delete cascade,
                       created_at     timestamp not null default current_timestamp,
                       updated_at     timestamp not null default current_timestamp,
                       is_checked_out boolean not null default false
);

create table cart_items (
                            id         bigserial primary key,
                            cart_id    uuid not null references carts(id) on delete cascade,
                            product_id bigint not null references products(id) on delete restrict,
                            qty        integer not null default 1 check (qty > 0),
                            price_locked integer, -- опционально: фиксируем цену на момент добавления
                            unique (cart_id, product_id)
);

create or replace function set_cart_updated_at()
returns trigger as $$
begin
  new.updated_at = current_timestamp;
return new;
end;
$$ language plpgsql;

create trigger trg_carts_updated
    before update on carts
    for each row execute function set_cart_updated_at();
