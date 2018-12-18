# NuclearDeterrence

NuclearDeterrence was a group project for a Video Game Programming class. It is designed in Java using asynchronous input, animated sprites, custom graphics, scrolling, sound fx, collision detection, and artificial intelligence.

---

# About

The game combines techniques taught in class and from Developing Games in Java 1st Edition. NuclearDeterrence is about an overhead helicopter fighter whose mission is to eliminate hostile enemies while scrolling through a tilemap in route to Linyi's nuclear silo.

---
 
# Features

>- Sprites
>- Projectiles
>- Animation
>- Polygon Model
>- Full Screen Manager
>- Game State Manager

---

# Code

The game is designed in Java using asynchronous input, animated sprites, custom graphics, scrolling, sound FX, collision detection, and artificial intelligence. Region detection is used to determine if an enemy is visible on the screen. Once an enemy is within distance of the player, the enemy will turn and face the player then attack.

---

# Graphics

The graphics are a mix of custom creations to free 3D models. The 3D models were imported into 3D Studio Max and altered before exporting as 2D images. The animations are image sequences and the background is generated using tile images. The sprites are overlaid on top of multiple scrolling tile maps. Adobe Photoshop and After Effects were also used for final image and animation polishing.

The game menu was designed by placing a camera inside a 3D helicopter model. A still image was exported and then composited over the fiery explosion. The menu buttons are strings drawn in Java. Invisible rectangles are placed around the text for collision detection with the mouse to determine which button was clicked.

<img src="https://user-images.githubusercontent.com/6556090/31797429-2cafd7d4-b4fc-11e7-8bb0-24731307764a.jpg">

<img src="https://user-images.githubusercontent.com/6556090/31797604-2380f912-b4fd-11e7-9777-95efe2006c66.jpg">

<img src="https://user-images.githubusercontent.com/6556090/31797606-2464f9fa-b4fd-11e7-9ff5-85660912a11e.jpg">

---

# Collision Detection

The process of handling collisions was designed to be scalable. Collision models were created with circles and rectangles. This allows for a more accurate result in calculating collisions with odd shaped objects. For example, in order to calculate clipping for the helicopters, two circles are used since the rotor blades extend further out than the body of the sprite.

Using this method allowed handling of two or more collision objects for a single sprite. To simplify the handling of multiple collision models, a collision class was created for each model that had more than one object. For instance, the helicopters used a custom class that stored values for two circles.

The collision model is then casted as an object in the sprite class. This way no matter what type of collision model was used, each sprite had one instance of a collision model. If a new sprite was created but required a new collision model, this does not affect the sprite class or require collision methods to be altered. Instead a new method could be generated to detect the new collision model. Thus this made the collision model scalable.

In addition, a rectangle was placed around the outer edge of the screen. This rectangle is used to calculate collision with objects in order to determine if the object was on or off screen. Only objects that are on screen get updated and drawn. This created an extra calculation against all objects every cycle, but reduced processing power by only drawing the visible objects. This was also helpful in saving processing against projectiles that flew off screen but were not yet removed from the game.

<img src="https://user-images.githubusercontent.com/6556090/31797677-8f1a419c-b4fd-11e7-9bed-cd12d0612f7e.jpg">

---

# Resources

>- Book: Developing Games in Java 1st Edition: by David Brackeen,‎ Bret Barker,‎ Lawrence Vanhelsuwe - ISBN-13: 978-1592730056, ISBN-10: 1592730051
