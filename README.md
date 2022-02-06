<div align="center">
  <a href="https://github.com/zackads/landmarkist">
    <img src="logo.png" alt="Logo" width="80" height="80">
  </a>
<h1 align="center">Landmarkist</h1>
  <p align="center">
    A web app that helps you discover interesting landmarks near you.
    <br />
    <a href="https://www.landmarkist.com">View live app</a>
    ·
    <a href="https://github.com/zackads/landmarkist/issues">Report a bug</a>
    ·
    <a href="https://github.com/zackads/landmarkist/issues">Request a feature</a>
  </p>
</div>

## About the project

![Screenshot](screenshot.png)

## Built with

* [Spring Boot](https://spring.io)
* [React](https://reactjs.org/docs/create-a-new-react-app.html)
* [Mapbox GL JS](https://www.mapbox.com/mapbox-gljs)

## Getting started

### Pre-requisities

* Java version 17 (I use [OpenJDK](https://openjdk.java.net/))
* [nvm](https://github.com/nvm-sh/nvm) version 0.38 or later

### API development

```bash
$ docker compose up db && ./gradlew clean bootRun
```

### Frontend development

```bash
$ cd src/main/reactapp
$ nvm use
$ npm start
```

## Usage

* Go to your location
* See details about landmark

## Roadmap

See the [open issues](https://github.com/zackads/landmarkist/issues) for a full list of proposed features and known
issues.

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.
