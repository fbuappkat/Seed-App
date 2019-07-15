Group Project 
===

# KAT

## Table of Contents 
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Microinvestment platform between philanthropists and entrepeneurs in developing countries. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Finance/Social Good
- **Mobile:** images/video, push notifications, map, connection to other apps 
- **Story:** return on investment, connection to the person and their project, entrepeneurs with less resources find funds 
- **Market:** entrepeneurs in developing countries, philanthropists 
- **Habit:** social aspect, progress, invest in additional projects
- **Scope:** varied (as little or much as we want)

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can Create Account / Login and out
* Entrepeneur can make a project page 
    * Entrepeneur can post breakdown of money needed for project
    * Investor can chose what part of project breakdown to invest in
* Investor can see timeline of projects they've invested in
* Entrepeneur can post progress updates
* Investor can pay entrepeneur
* Entrepeneur can receive and keep track of funds
* Entrepeneur can keep a bank account of funds they've recieved from investors and money they've made from their project
* Investor can view what percentage of shares they have in a project
    * Shares are calculated based on how large of a donation the investor makes based on the overall cost of the project

* Investors can recieve a return on their investment when an entrepeneur makes money from a project
* User can view their profile page 
    * Profile page includes investments and projects

**Optional Nice-to-have Stories**

* See where entrepeneur is on map 
* Investor can chat with entrepeneur directly 
* Investor can get recommendations of projects to invest in based on their interests 
* Investor can chose categories they're interested in
* Entrepeneur can chose what category their project is in
* Entrepeneurs can find teammates to work with by seeing local people working on similar projects 
* Entrepeneurs can post videos / photos of their project
* Filters for finding projects (Popular, new, by location)
* Verification of users and their projects
* User can comment on projects
* User can share projects on social media 
* User can follow other entrepeneurs / investors 

### 2. Screen Archetypes

* Login Screen 
   * User can login
   * Hollywood house forgot password
* Registration screen
   * User can create a new account
* Feed screen
    * User can see posted updates on projects they invested in 
* Profile screen
   * View projects invested in
   * View projects worked on
   * See amount donated towards projects
   * See amount raised for personal projects
   * Display resume for user
   * Display amount earned from overall projects 
* Projects screen
    * Displays a list of all projects user has invested in / worked on
* Details Projects screen
   * User can create a new project
   * User can press a button to compose a new post 
   * Entrepeneur can toggle between their different projects
   * Entrepeneur can view a breakdown of their BoM and what has been invested in and what hasn't
   * Toggle between investments and current projects
   * Within a project, user can click on an "invest" button to buy an item and invest in the project 
   
* Create a new project screen
    * User can create a title for project
    * User can write a description for project
    * User can create a BoM for projects 
* Compose a new post 
    * User can write and post an update about a project
* Edit project screen
    * User can edit the BoM, title, and description of a project
    * (can be same as create a project screen potentially)
* Investing screen
    * Displays the item and project the user has chosen to invest in 
    * Displays what share percentage of the project the user would recieve if they chose to invest in that project 
    * User can chose a way to pay for specified item 


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Feed
* Projects
* User profile

**Flow Navigation** (Screen to Screen)

* Login Screen
   * => Feed
* Registration Screen
   * => Feed
* Profile Screen
   * => Projects 
* Projects Screen
   * => Details of Project
* Deatils of Projects Screen
   * => Investment 
   * => Create a new project
   * => Compose a new post 

## Wireframes
### Handsketched wireframes 
<img src="https://github.com/fbuappkat/team_project/blob/master/paper_wireframe_1.jpg?raw=true" width=600>
<img src="https://github.com/fbuappkat/team_project/blob/master/paper_wireframe_2.jpg?raw=true" width=600>

### [BONUS] Digital Wireframes & Mockups (Interactive Prototype)
https://www.figma.com/file/HNKcoHLYXYJmhOmzTIvbhJIo/Wireframe?node-id=0%3A1

## Schema 
[This section will be completed in Unit 9]
### Models
* **User**
    * objectId
        * String
            * unique ID for the user (Default Field)
    * username
        * String
            * Unique username 
    * Email 
        * String
            * Unique email
    * Location
        * Location object or string 
            * Location of user
    * Handle
        * String
            * Shown name for posts and profile
    * Profile Image
        * ParseFile
            * Image for a particular users profile and posts
    * Bio
        * String
            * User can write their own background, displayed on profile
    * CreatedAt
        * DateTime
            * Time at which account was created (Default Field)

* **Post**
    * ObjectId 
        * String
            * Unique Id for post (Default Field)
    * User
        * Pointer to User
            * User that created post
    * Project
        * Pointer to Project
            * project that post pertains to
    * Caption 
        * String
            * Description of post to be displayed with the post
    * Media
        * ParseFile
            * Image or video of post content (optional)
    * Like count
        * Integer 
            * Number of likes a post has
    * Comment count
        * Integer 
            * Number of comments a post has
    * CreatedAt
        * DateTime
            * Time at which post was created (Default Field)

* **Comment**
    * ObjectId
        * String
            * Unique Id for comment (Default Field)
    * Text
        * String
            * Comment itself
    * Post
        * Pointer to post
            * Post that comment is on
    * Created At 
        * DateTime
            * Time at which comment was created (Default Field)

* **Project/Startup**
    * ObjectId
        * String
            * Unique Id for project (Default Field)
    * User
        * Pointer to User
            * User that created post
    * Description 
        * String
            * Description of project 
    * Media
        * ParseFile
            * Images or videos associated with project
    * Requested Funds
        * Float
            * Money asked for by project creator 
    * Invested Funds
        * Float
            * Current amount of funds already invested 
    * Investors
        * User Array
            * List of investors of project 
    * Followers
        * User Array
            * List of Followers of project 
    * CreatedAt 
        * DateTime
            * Time at which project was made (Defaul Field)
