export class Camera {
  id = "";
  name = "";
  url = "";
  urlTag = "";
  previewUrl = "";
  previewUrlTag = "";

  constructor(name: string,
              url: string,
              urlTag: string,
              previewUrl: string,
              previewUrlTag: string) {
    this.name = name;
    this.url = url;
    this.urlTag = urlTag;
    this.previewUrl = previewUrl;
    this.previewUrlTag = previewUrlTag;
  }
}
