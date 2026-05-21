package com.example.data

data class Review(
    val author: String,
    val rating: Double,
    val comment: String,
    val date: String
)

data class Restaurant(
    val id: String,
    val name: String,
    val imageUrl: String,
    val cuisine: String,
    val rating: Double,
    val reviewsCount: Int,
    val priceRange: String, // "$", "$$", "$$$"
    val distance: Double, // in miles
    val address: String,
    val description: String,
    val reviews: List<Review>
)

object RestaurantData {
    val localRestaurants = listOf(
        Restaurant(
            id = "rest_1",
            name = "Sakura Zen Sushi",
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=600&auto=format&fit=crop",
            cuisine = "Japanese / Sushi",
            rating = 4.8,
            reviewsCount = 124,
            priceRange = "$$$",
            distance = 0.8,
            address = "742 Blossom Way, Downtown",
            description = "Elegant Japanese dining featuring masterfully prepared sushi, sashimi, and warm sake in a serene, bamboo-accented room.",
            reviews = listOf(
                Review("Emily K.", 5.0, "Absolute heaven! The salmon belly melts in your mouth and the presentation is like modern art.", "2 days ago"),
                Review("Marcus T.", 4.5, "Premium prices but worth every cent. Extremely fresh fish and superb service.", "1 week ago"),
                Review("Sora L.", 5.0, "The Omakase is a journey. Highlight of our anniversary trip!", "3 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_2",
            name = "El Barrio Taqueria",
            imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?q=80&w=600&auto=format&fit=crop",
            cuisine = "Mexican / Street Food",
            rating = 4.5,
            reviewsCount = 310,
            priceRange = "$",
            distance = 1.2,
            address = "319 Fiesta Boulevard, Mission District",
            description = "A colorful, high-energy joint serving legendary birria tacos, fresh handmade corn tortillas, and smoky habanero salsas.",
            reviews = listOf(
                Review("Diego R.", 5.0, "Best birria in the city, hands down. Dip it in the consome and prepare for pure joy.", "Yesterday"),
                Review("Jessica P.", 4.0, "Lines are always long but they move fast. The al pastor is outstanding.", "5 days ago")
            )
        ),
        Restaurant(
            id = "rest_3",
            name = "The Copper Hearth Grill",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=600&auto=format&fit=crop",
            cuisine = "American / Burgers",
            rating = 4.6,
            reviewsCount = 185,
            priceRange = "$$",
            distance = 2.5,
            address = "104 Woodlawn Drive",
            description = "Gourmet wood-fired burgers, artisanal craft beers on tap, and hand-cut truffle fries in an upscale rustic brewpub.",
            reviews = listOf(
                Review("Brad H.", 5.0, "The Smoked Gouda Burger is monstrously good. Truffle fries are highly addictive.", "3 days ago"),
                Review("Sarah G.", 4.0, "Cozy ambiance, solid list of local draft beers, burgers are juicy and grilled to absolute perfection.", "2 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_4",
            name = "Bella Italia Trattoria",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600&auto=format&fit=crop",
            cuisine = "Italian / Pasta",
            rating = 4.7,
            reviewsCount = 215,
            priceRange = "$$",
            distance = 1.9,
            address = "85 Tuscan Plaza, Little Italy",
            description = "Classic, family-owned trattoria specializing in handmade pasta, brick-oven thin crust pizzas, and excellent house wines.",
            reviews = listOf(
                Review("Sophia V.", 5.0, "Tastes like my grandmother's cooking in Naples. The wild boar pappardelle is fantastic.", "Yesterday"),
                Review("Arthur M.", 4.5, "Warm, intimate atmosphere with candlelight. The brick-oven Margherita pizza has perfect chew.", "1 week ago")
            )
        ),
        Restaurant(
            id = "rest_5",
            name = "Cozy Mug Cafe & Roastery",
            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?q=80&w=600&auto=format&fit=crop",
            cuisine = "Cafe / Desserts",
            rating = 4.3,
            reviewsCount = 98,
            priceRange = "$",
            distance = 0.5,
            address = "12 Main Street, Corner Crossing",
            description = "A warm, light-flooded sanctuary serving house-roasted single-origin coffees, flaky croissants, and decadent seasonal pastries.",
            reviews = listOf(
                Review("Elena B.", 5.5, "Perfect spot to work or read a book. The lavender honey latte is sublime.", "4 days ago"),
                Review("Tim D.", 4.0, "Great pour-overs and very friendly baristas, though seating space is quite limited.", "2 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_6",
            name = "Mirch Masala Bistro",
            imageUrl = "https://images.unsplash.com/photo-1585938338392-50a59910d6e5?q=80&w=600&auto=format&fit=crop",
            cuisine = "Indian / Curry",
            rating = 4.4,
            reviewsCount = 142,
            priceRange = "$$",
            distance = 3.1,
            address = "52 Spice Lane, East End",
            description = "Vibrant, aromatic regional Indian specialties, buttery garlic naan baked fresh, and a modern twist on popular street snacks.",
            reviews = listOf(
                Review("Rajit S.", 5.0, "Incredible depth of flavor in the chicken tikka masala. Tastes authentic.", "4 days ago"),
                Review("Chloe Bennett", 4.0, "We loved the samosa chat and paneer makhani. Spicy, rich, and delicious.", "1 month ago")
            )
        ),
        Restaurant(
            id = "rest_7",
            name = "Pho Saigon Palace",
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad8cd305?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Pho",
            rating = 4.6,
            reviewsCount = 260,
            priceRange = "$",
            distance = 2.1,
            address = "118 Lotus Ave, Plaza District",
            description = "A local favorite for giant bowls of steaming noodle soup, aromatic 24-hour beef bone broth, and crispy spring rolls.",
            reviews = listOf(
                Review("Nhat T.", 5.0, "This broth is liquid gold. Clean, spice-forward, and perfect for cold rainy days.", "Yesterday"),
                Review("Mary W.", 4.0, "Generous portions, cheap prices, extremely fast service! Love their pho dac biet.", "3 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_8",
            name = "Le Petit Croissant",
            imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=600&auto=format&fit=crop",
            cuisine = "French / Bakery",
            rating = 4.8,
            reviewsCount = 155,
            priceRange = "$$",
            distance = 0.9,
            address = "404 Eiffel Lane",
            description = "An authentic French boulangerie with buttery viennoiseries, sourdough loaves, sweet tarts, and freshly squeezed lemonades.",
            reviews = listOf(
                Review("Luc D.", 5.0, "As a French expat, this is the only bakery that gets croissants right. Flaky, buttery layers!", "3 days ago"),
                Review("Amelie F.", 4.5, "Their almond croissants are out of this world. Highly recommend arriving early.", "2 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_9",
            name = "The Green Leaf Eatery",
            imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vegan / Vegetarian / Salad",
            rating = 4.5,
            reviewsCount = 112,
            priceRange = "$$",
            distance = 1.5,
            address = "220 Health Plaza",
            description = "Nutritious, high-vibrational plant-based bowl creations, fresh cold-pressed wellness shots, and sugar-free gluten-free bakery items.",
            reviews = listOf(
                Review("Gary S.", 5.5, "Proof that healthy food can explode with flavor. The Buddha Bowl is dynamic and filing.", "5 days ago"),
                Review("Rachel O.", 4.0, "Creative dishes, amazing juices, and lovely modern clean aesthetic. Clean vibes.", "3 weeks ago")
            )
        ),
        Restaurant(
            id = "rest_10",
            name = "Seoul Soul BBQ",
            imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?q=80&w=600&auto=format&fit=crop", // Ramen or Grill placeholder, beautiful Asian food image
            cuisine = "Korean / BBQ",
            rating = 4.7,
            reviewsCount = 190,
            priceRange = "$$$",
            distance = 2.8,
            address = "550 K-Way, Koreatown",
            description = "Interactive tabletop grills, premium marbled short rib (Galbi), a massive array of banchan side dishes, and cold draft beers.",
            reviews = listOf(
                Review("Ji-Min K.", 5.0, "Galbi melts in your mouth. The staff cuts the meat for you and is incredibly nice.", "6 days ago"),
                Review("John D.", 4.5, "Awesome group dining spot! Get the pork belly and kimchi stew, and definitely try the soju.", "2 weeks ago")
            )
        )
    )
}
