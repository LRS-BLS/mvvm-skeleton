# mvvm-skeleton

### Intro

Mvvm Skeleton is a tiny Android MVVM Framework we came up with. This sample application presents capabilities of a framework, but is not limited to them. Feel free to extend or contribute with additional features you may need.
It utilises AndroidViewModel from Architecture Components Library, using Single Activity and Multiple Fragment approach. 
Framework is responsible for handling navigation with support of multiple Regions(area of a screen that is capable of displaying a fragment)

### Getting started

To add a screen you have to follow these steps, order may be changed if you prefer to do a diffarent approach:
- create a ViewModelClass that extends a BaseViewModel. Think of properties and methods your view will be using.
- create a layout file that utilises a DataBinding property of a created ViewModel 
- create a View(fragment) class that extends a BaseFragment, add definition of Binding and ViewModel class to template SuperClass invocation. If you need to add more code to your fragment you only need to implement BaseView interface so our framework can bind View with a ViewModel
- in ScreenContextEnum add a binding definition and add a navigation screen definition that will be used to trigger a navigation to this particular screen

To navigate to your screen use NavigationService's navigate method. 
