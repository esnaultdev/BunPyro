# Bunpro API

## API docs

The API documented at:
- https://bunpro.jp/api/
- https://bunpro.jp/api/v3/docs
- https://bunpro.jp/api/v4/docs

Note that the v3 and v4 APIs are not officially public.

## Nullability

The Bunpro API has two flaws:
- there is no documentation about which fields are required.
- even for fields that are required in practice, they might become nullable in the future. This
happened twice, and a hotfix was needed for the app.

Based on that, all entities that we declare in the app have all their fields nullable. The non
nullability is then enforced when mapping the network entities to our domain our database entities.

This is not ideal, but at least it prevents errors in production.
