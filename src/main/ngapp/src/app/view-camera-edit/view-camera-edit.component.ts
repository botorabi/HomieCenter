import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ApiDeviceService} from "../service/api-device.service";
import {Camera} from "../service/camera";
import {AppInformationService} from "../service/app-information.service";
import {DialogTwoButtonsComponent} from "../dialog-two-buttons/dialog-two-buttons.component";
import {MatDialog} from "@angular/material";

@Component({
  selector: 'app-view-camera-edit',
  templateUrl: './view-camera-edit.component.html',
  styleUrls: ['./view-camera-edit.component.css']
})
export class ViewCameraEditComponent implements OnInit {

  camera: Camera;
  error: string;

  constructor(private router: Router,
              private appInfoService: AppInformationService,
              private apiDeviceService: ApiDeviceService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    if (!this.appInfoService.selectedCamera) {
      this.camera = new Camera("", "", "", "", "");
    }
    else {
      this.camera = this.appInfoService.selectedCamera;
    }
    this.appInfoService.refreshLogoutTimer();
  }

  public onApply() {
    this.apiDeviceService.cameraCreateOrUpdate(this.camera,(camera: Camera, error: string) => {
      if (error) {
        this.error = error;
      }
      else
        this.router.navigate([""]);
      });
  }

  public onDelete() {
    this.openDialogConfirmDeletion(this.camera);
  }

  private openDialogConfirmDeletion(camera: Camera) : void {
    const dialogRef = this.dialog.open(DialogTwoButtonsComponent);
    let dialog = dialogRef.componentInstance;
    dialog.setTitle('Delete ' + camera.name);
    dialog.setContent('Do you really want to delete the camera?');
    dialog.setButton1Text("No");
    dialog.setButton2Text("Yes");
    dialogRef.afterClosed().subscribe(result => {
      if (result === "Yes") {
        this.deleteCamera(camera);
      }
    });
  }

  private deleteCamera(camera: Camera) {
    this.apiDeviceService.cameraDelete(camera, (success: boolean) => {
      if (!success) {
        this.error = "Could not delete Camera!";
      }
      else
        this.router.navigate([""]);
    });
  }
}
