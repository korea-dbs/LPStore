
import { Module } from '@nestjs/common';
import { OutModule } from '../out/out.module';
import { HostController } from './host.controller';
import { SetupController } from './setup.controller';
import { WorkController } from './work.controller';
import { AdbController } from './adb.controller';

@Module({
  imports: [OutModule],
  controllers: [SetupController, AdbController, HostController, WorkController],
  providers: [SetupController, AdbController, HostController, WorkController],
})
export class InModule {}
