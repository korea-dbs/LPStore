import * as path from 'path';
import { env } from 'process';
export const AndroidPath = {
  Query: '/sdcard/queries',
  ExternalDB:
  '/data/user/0/com.android.providers.media.module/databases/external.db',
  // '/data/user/0/com.android.providers.media.module/databases/external.continuous.db',
  // '/data/user/0/com.android.providers.media.module/databases/external.db',
  // '/data/user/0/com.android.providers.media.module/databases/external.db',
  // '/data/user/0/com.android.providers.media.module/databases/external.db',
  // '/data/user/0/com.android.providers.media.module/databases/external.db',
};

export const HostPath = {
  Source: path.resolve(process.cwd(), 'sources'),
  Query: path.resolve(process.cwd(), 'sources', 'queries'),
  Workspace: env.WORK_DIR
};
export const sourcesPath = path.resolve(process.cwd(), 'sources');
export const workspacePath = env.WORK_DIR;
