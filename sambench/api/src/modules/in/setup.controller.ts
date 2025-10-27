import { AdbController } from './adb.controller';
import {
  Controller,
  HttpStatus,
  OnModuleInit,
  Post,
  Put,
  Query,
  Res,
} from '@nestjs/common';
import { ApiQuery, ApiTags } from '@nestjs/swagger';
import { Response } from 'express';
import ADB from 'appium-adb';
import * as path from 'path';
import { AndroidPath, HostPath, sourcesPath } from 'src/utils/const';
import { sleep } from 'src/utils/utils';

/**
 * Controller for ADB operations.
 */
@ApiTags('Setup')
@Controller('setup')
export class SetupController {
  constructor(private readonly adbService: AdbController) {
  }

  /**
   * Generates a batch of storage files.
   * @param {number} batches The number of batches to generate.
   * @param {number} imgRatio The ratio of image files.
   * @param {number} xmpRatio The ratio of XMP files.
   * @returns The count of generated files.
   */
  @ApiQuery({ name: 'batches', type: Number, description: 'The number of batches to generate', required: false, example: 100 })
  @ApiQuery({ name: 'imgRatio', type: Number, description: 'The ratio of image files', required: false, example: 90 })
  @ApiQuery({ name: 'xmpRatio', type: Number, description: 'The ratio of XMP files', required: false, example: 10 })
  @Put('storage/generate-batch')
  async generateStorageBatch(
    @Query('batches') batches: number = 100,
    @Query('imgRatio') imgRatio: number = 90,
    @Query('xmpRatio') xmpRatio: number = 10,
  ) {
    const ratioSum = imgRatio + xmpRatio;
    const sources = ['img.jpeg', 'xmp.jpeg'];
    const sourcesRatio = [imgRatio, xmpRatio];
    const sourceCounts: { [key: string]: number } = {};

    sources.forEach((source, i) => {
      sourceCounts[source] = Math.round((sourcesRatio[i] / ratioSum) * batches);
    });

    if (!this.adbService.adb) {
      throw new Error('adb is not initialized');
    }

    await this.adbService.shell('mkdir -p /sdcard/DCIM');

    for (const source of sources) {
      console.log('push', source);
      await this.adbService.adb.push(
        path.resolve(sourcesPath, 'images', source),
        `/sdcard/DCIM/${source}`,
      );
      await sleep(1000);
    }

    await this.adbService.shell('rm -rf /sdcard/DCIM/batch');
    await this.adbService.shell('mkdir -p /sdcard/DCIM/batch');

    for (const source in sourceCounts) {
      const count = sourceCounts[source];
      for (let i = 0; i < count; i++) {
        const indexString = i.toString().padStart(3, '0');
        await this.adbService.shell(
          `cp /sdcard/DCIM/${source} /sdcard/DCIM/batch/${indexString}-${source}`,
        );
      }
    }
    return {
      count: sourceCounts,
    };
  }

  @Put('storage/push-scripts')
  async pushScripts() {
    const outputs = [];

    outputs.push(await this.adbService.shell('mkdir -p /data/local/fullscan'));

    outputs.push(await this.adbService.pushFile(
      path.join(HostPath.Source, 'scripts/cal_blk_offset.sh'),
      "/data/local/fullscan/cal_blk_offset.sh"
    ));

    outputs.push(await this.adbService.pushFile(
      path.join(HostPath.Source, 'scripts/frag_count.sh'),
      "/data/local/fullscan/frag_count.sh"
    ));

    outputs.push(await this.adbService.pushFile(
      path.join(HostPath.Source, 'scripts/max_dis.sh'),
      "/data/local/fullscan/max_dis.sh"
    ));

    return { message: "스크립트가 성공적으로 업로드되었습니다.", outputs };
  }


  /**
   * Pushes a query file to the device.
   */
  @Put('storage/push-query')
  async pushQuery() {
    if (!this.adbService.adb) {
      throw new Error('adb is not initialized');
    }

    await this.adbService.shell('rm -rf /sdcard/queries');
    await this.adbService.pushFile(path.join(sourcesPath, 'queries'), AndroidPath.Query);
  }
}