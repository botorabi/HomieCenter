export class Camera {
  id = "";
  name = "";
  url = "";
  previewUrl = "";

  constructor(name: string, url: string, previewUrl: string) {
    this.name = name;
    this.url = url;
    this.previewUrl = previewUrl;
  }
}
