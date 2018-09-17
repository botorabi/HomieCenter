import {Component, OnInit, ViewChild} from '@angular/core';
import {User} from "../service/user";
import {ApiUserService} from "../service/api-user.service";
import {MatDialog, MatPaginator, MatTableDataSource} from "@angular/material";
import {AppInformationService} from "../service/app-information.service";
import {DialogTwoButtonsComponent} from "../dialog-two-buttons/dialog-two-buttons.component";

@Component({
  selector: 'app-view-user',
  templateUrl: './view-user.component.html',
  styleUrls: ['./view-user.component.css']
})
export class ViewUserComponent implements OnInit {

  displayedColumns = ['realName', 'userName', 'link'];
  dataSource = new MatTableDataSource<User>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  error: string;

  constructor(private apiUserService: ApiUserService,
              public appInfoService: AppInformationService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    this.appInfoService.refreshLogoutTimer();
    this.getUsers();
  }

  private getUsers() {
    this.apiUserService.getUsers((users: Array<User>) => {
      this.dataSource.data = users;
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  onDelete(user: User) {
    this.openDialogConfirmDeletion(user);
  }

  private openDialogConfirmDeletion(user: User) : void {
    const dialogRef = this.dialog.open(DialogTwoButtonsComponent);
    let dialog = dialogRef.componentInstance;
    dialog.setTitle('Delete User');
    dialog.setContent('Do you really want to delete user ' + user.realName + '?');
    dialog.setButton1Text("No");
    dialog.setButton2Text("Yes");
    dialogRef.afterClosed().subscribe(result => {
      if (result === "Yes") {
        this.deleteUser(user.id);
      }
    });
  }

  private deleteUser(userId: number) {
    this.error = null;
    this.apiUserService.deleteUser(userId, (success: boolean) => {
      if (success) {
        this.getUsers();
      }
      else {
        this.error = "Could not delete User!";
      }
    });
  }

  isOwner(userName: string) {
    return this.appInfoService.userStatus.name == userName;
  }
}
