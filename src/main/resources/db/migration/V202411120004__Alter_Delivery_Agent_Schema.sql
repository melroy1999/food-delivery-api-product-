alter table delivery_agents
    add column is_active BOOLEAN NOT NULL DEFAULT true;

alter table delivery_agents
    add column is_working BOOLEAN NOT NULL DEFAULT true;