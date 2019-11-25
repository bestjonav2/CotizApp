import 'package:flutter/material.dart';
import 'models/cotization.dart';
import 'screens/hello_world.dart';

import 'models/global.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'CotizApp',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _count = 0;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: backgroundColor,
        child: Column(
          children: <Widget>[
            Stack(
              children: <Widget>[
                Container(
                  padding: EdgeInsets.all(40),
                  constraints: BoxConstraints.expand(height: 225),
                  decoration: BoxDecoration(
                      gradient: new LinearGradient(
                          colors: [lightTop, darkTop],
                          begin: const FractionalOffset(1.0, 1.0),
                          end: const FractionalOffset(0.2, 0.2),
                          stops: [0.0, 1.0],
                          tileMode: TileMode.clamp),
                      borderRadius: BorderRadius.only(
                          bottomLeft: Radius.circular(30),
                          bottomRight: Radius.circular(30))),
                  child: Container(
                    padding: EdgeInsets.only(top: 50),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          'CotizApp',
                          style: titleStyleWhite,
                        )
                      ],
                    ),
                  ),
                ),
                Container(
                  margin: EdgeInsets.only(top: 120),
                  constraints: BoxConstraints.expand(height: 550),
                  child: ListView(
                      padding: EdgeInsets.only(left: 40),
                      scrollDirection: Axis.horizontal,
                      children: getRecentJobs()),
                ),
              ],
            )
          ],
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        shape: const CircularNotchedRectangle(),
        child: Container(
          height: 50.0,
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.of(context)
              .push(MaterialPageRoute(builder: (context) => HelloWorld()));
        },
        tooltip: 'Create cotization',
        child: Icon(Icons.add),
        backgroundColor: lightBlueIsh,
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
    );
  }

  List<Cotization> findJobs() {
    List<Cotization> cotizations = [];
    for (int i = 0; i < 10; i++) {
      cotizations.add(new Cotization("MS-7877", "Pieza de motor de audi R8",
          420.69, new AssetImage("assets/img/images.png")));
    }
    return cotizations;
  }

  String formatCost(double salary) {
    return "\$" + salary.toString();
  }

  List<Widget> getRecentJobs() {
    List<Widget> recentJobCards = [];
    List<Cotization> cots = findJobs();
    for (Cotization c in cots) {
      recentJobCards.add(getJobCard(c));
    }
    return recentJobCards;
  }

  Widget getJobCard(Cotization c) {
    return Container(
      padding: EdgeInsets.all(10),
      margin: EdgeInsets.only(right: 20, bottom: 30, top: 30),
      width: 300,
      decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            new BoxShadow(
              color: Colors.grey,
              blurRadius: 10.0,
            ),
          ],
          borderRadius: BorderRadius.all(Radius.circular(15))),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: <Widget>[
          Column(
            children: <Widget>[
              Text(
                "Cotization #" + c.id,
                style: cardTitleStyle,
              ),
              Container(
                height: 250.0,
                width: double.maxFinite,
                child: FittedBox(
                  child: Image.network(
                      "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSGOd0cIAPhNTV8HXnfsgIm8MLGB2SkgTtXrzP4qCnwD7dhSZhZ"),
                  fit: BoxFit.cover,
                ),
              ),
              Text(
                c.description + " / " + _count.toString(),
                style: cardBodyStyle,
              ),
              Container(
                margin: EdgeInsets.only(top: 15),
                child: Text(formatCost(c.averageCost), style: costStyle),
              ),
              Container(
                margin: EdgeInsets.only(top: 15),
                width: 150,
                child: RaisedButton(
                  padding: const EdgeInsets.all(8.0),
                  textColor: darkTop,
                  color: Colors.white,
                  onPressed: viewDetails,
                  child: new Text("View more"),
                  shape: RoundedRectangleBorder(
                      borderRadius: new BorderRadius.circular(18.0),
                      side: BorderSide(color: darkTop)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void viewDetails() {}
}
