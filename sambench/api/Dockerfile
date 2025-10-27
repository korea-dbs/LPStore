FROM node:18-alpine AS builder
WORKDIR /usr/src/app

# Copy Dependencies
COPY package.json ./
COPY yarn.lock ./

# Install Dependencies
RUN yarn install --production --frozen-lockfile --non-interactive
RUN cp -R node_modules prod_node_modules
RUN yarn install --frozen-lockfile --non-interactive

# Copy Code
COPY . .

# Build
RUN yarn build

################################################################

FROM node:18-alpine AS server
ARG SERVICE_NAME
WORKDIR /usr/src/app

COPY package.json ./package.json
COPY --from=builder /usr/src/app .

CMD ["yarn", "start:prod"]