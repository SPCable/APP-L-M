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
            name = "Phở Thìn Lò Đúc",
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad8cd305?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Pho",
            rating = 4.7,
            reviewsCount = 2850,
            priceRange = "$$",
            distance = 0.6,
            address = "13 Lò Đúc, Hai Bà Trưng, Hà Nội",
            description = "Nổi tiếng với món phở bò tái lăn xào lăn thơm nức mũi cùng lượng hành hoa đặc trưng phủ ngập bát nước dùng béo ngậy đậm đà hương vị truyền thống.",
            reviews = listOf(
                Review("Anh Tuấn", 5.0, "Nước dùng béo ngậy, bò xào rất thơm. Rất nhiều hành, cực kỳ hợp gu của mình!", "2 ngày trước"),
                Review("Mai Chi", 4.0, "Hơi béo và nhiều mỡ một chút đối với ai thích phở truyền thống thanh vị, nhưng bò tái lăn thì đỉnh thực sự.", "1 tuần trước"),
                Review("David L.", 5.0, "An absolute legendary Pho experience in Hanoi. A must-visit place!", "3 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_2",
            name = "Bún Chả Hương Liên (Obama)",
            imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Bun Cha",
            rating = 4.5,
            reviewsCount = 4200,
            priceRange = "$$",
            distance = 0.8,
            address = "24 Lê Văn Hưu, Hai Bà Trưng, Hà Nội",
            description = "Quán bún chả nổi tiếng thế giới sau chuyến viếng thăm lịch sử của cựu Tổng thống Mỹ Barack Obama năm 2016. Chả nướng thơm phức ăn kèm nước chấm chua ngọt trứ danh.",
            reviews = listOf(
                Review("Quốc Bảo", 5.0, "Chả nướng rất thơm, không bị cháy. Nem hải sản giòn rụm béo ngậy. Không gian lưu giữ nhiều kỷ niệm của tổng thống.", "Hôm qua"),
                Review("Thanh Thảo", 4.0, "Khách du lịch cực kỳ đông vào giờ cao điểm nhưng phục vụ rất nhanh nhẹn. Vẫn giữ vững phong độ.", "5 ngày trước")
            )
        ),
        Restaurant(
            id = "rest_3",
            name = "Cà Phê Giảng (Egg Coffee)",
            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?q=80&w=600&auto=format&fit=crop",
            cuisine = "Cafe / Egg Coffee",
            rating = 4.6,
            reviewsCount = 3100,
            priceRange = "$",
            distance = 0.4,
            address = "39 Nguyễn Hữu Huân, Hoàn Kiếm, Hà Nội",
            description = "Nơi khai sinh ra món cà phê trứng huyền thoại của Hà Nội từ năm 1946. Vị béo ngậy, mịn màng của lòng đỏ trứng gà đánh bông kết hợp hoàn hảo cùng hương vị cà phê đậm đà.",
            reviews = listOf(
                Review("Hoàng Nam", 5.0, "Cà phê trứng nóng ngon đỉnh cao, ngậy béo mịn màng như kem caramel nhưng vẫn dậy mùi cà phê.", "3 ngày trước"),
                Review("Minh Trang", 4.5, "Lối đi vào ngõ nhỏ cổ kính đậm chất Hà Nội xưa. Đồ uống rất ngon, giá cả vô cùng hợp lý.", "2 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_4",
            name = "Pizza 4P's Tràng Tiền",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=600&auto=format&fit=crop",
            cuisine = "Italian / Pizza / Fusion",
            rating = 4.8,
            reviewsCount = 5200,
            priceRange = "$$$",
            distance = 1.1,
            address = "43 Tràng Tiền, Hoàn Kiếm, Hà Nội",
            description = "Thương hiệu pizza lò củi thủ công nổi tiếng với phô mai Burrata tự sản xuất siêu béo ngậy cực kỳ tươi ngon cùng dịch vụ chăm sóc khách hàng Omotenashi chuẩn Nhật.",
            reviews = listOf(
                Review("Linh Đan", 5.0, "Pizza 4 phô mai kèm mật ong luôn là chân ái. Phục vụ chu đáo, tận tình, không gian sang trọng ấm cúng.", "Hôm qua"),
                Review("Tuấn Anh", 5.0, "Món mì Ý cua siêu ngon béo ngậy. Pizza Burrata Parma Ham rất tươi. Cực kỳ đáng tiền!", "1 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_5",
            name = "Bún Chả Đắc Kim",
            imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Bun Cha",
            rating = 4.3,
            reviewsCount = 1850,
            priceRange = "$$",
            distance = 0.3,
            address = "1 Hàng Mành, Hoàn Kiếm, Hà Nội",
            description = "Một trong những quán bún chả lâu đời và nổi tiếng nhất khu phố cổ Hà Nội. Một suất siêu đầy đặn gồm chả băm, chả miếng nướng vàng ruộm cùng nem cua bể giòn rụm ngọt thịt.",
            reviews = listOf(
                Review("Quỳnh Anh", 4.0, "Suất ăn rất to, hai người ăn yếu có khi chung một suất được luôn. Nem cua bể ngập thịt giòn tan.", "4 ngày trước"),
                Review("Thế Vinh", 4.0, "Chả nướng đậm đà, nước chấm ấm nóng vừa vị. Quán nằm ngay góc phố cổ nhộn nhịp thú vị.", "2 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_6",
            name = "Bánh Mì 25",
            imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Banh Mi",
            rating = 4.6,
            reviewsCount = 3900,
            priceRange = "$",
            distance = 0.5,
            address = "25 Hàng Cá, Hoàn Kiếm, Hà Nội",
            description = "Quán bánh mì kẹp pa-tê thập cẩm trứ danh cực kỳ hút khách du lịch nhờ vỏ bánh giòn rụm bên ngoài kết hợp phần nhân thịt nướng thơm lừng, pa-tê nhà làm ngậy béo và dưa góp thanh mát.",
            reviews = listOf(
                Review("Ngọc Hải", 5.0, "Vỏ bánh mì giòn xốp tuyệt vời, pate siêu thơm và không bị hôi. Xứng đáng là điểm dừng chân ẩm thực hàng đầu.", "4 ngày trước"),
                Review("Chloe B.", 5.0, "The best banh mi I have ever eaten. The pork is savory and sweet, herbs are so fresh!", "1 tháng trước")
            )
        ),
        Restaurant(
            id = "rest_7",
            name = "Chả Cá Lã Vọng",
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Cha Ca",
            rating = 4.2,
            reviewsCount = 1200,
            priceRange = "$$$",
            distance = 0.2,
            address = "14 Chả Cá, Hoàn Kiếm, Hà Nội",
            description = "Nhà hàng chả cá cổ kính nhất Hà Nội với lịch sử hơn 100 năm. Chuyên phục vụ chả cá lăng nướng nghệ vàng ruộm trên chảo nóng cùng hành hoa, thì là, ăn kèm bún sợi, lạc rang bùi bùi và mắm tôm chưng.",
            reviews = listOf(
                Review("Đức Huy", 4.0, "Hương vị cổ xưa đặc trưng Hà Nội. Cá thơm ngậy, ăn kèm mắm tôm pha sủi bọt thì chuẩn vị không đâu sánh bằng.", "Hôm qua"),
                Review("Hồng Ngọc", 4.0, "Không gian nhà cổ đậm nét rêu phong lịch sử. Giá hơi cao một chút nhưng là trải nghiệm rất đáng có khi tới Thủ đô.", "3 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_8",
            name = "Quán Ăn Ngon (Phan Bội Châu)",
            imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Street Food",
            rating = 4.4,
            reviewsCount = 4900,
            priceRange = "$$",
            distance = 1.3,
            address = "18 Phan Bội Châu, Hoàn Kiếm, Hà Nội",
            description = "Không gian biệt thự Pháp cổ rộng lớn mô phỏng các gian hàng chợ quê xưa nhộn nhịp, nơi hội tụ hàng trăm món ăn đường phố đặc sắc từ khắp 3 miền đất nước vô cùng sinh động.",
            reviews = listOf(
                Review("Thanh Lam", 5.0, "Rất thích hợp tiếp khách nước ngoài hoặc gia đình tụ họp. Đầy đủ các món ăn từ Nam ra Bắc được chế biến ngon, sạch sẽ.", "3 ngày trước"),
                Review("Pierre G.", 4.5, "A beautiful open courtyard with stalls making fresh food. Bánh xèo is exceptionally good!", "2 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_9",
            name = "Bún Đậu Ngõ Trạm",
            imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Bun Dau",
            rating = 4.5,
            reviewsCount = 920,
            priceRange = "$$",
            distance = 0.5,
            address = "1B Ngõ Trạm, Hoàn Kiếm, Hà Nội",
            description = "Mẹt bún đậu mắm tôm siêu đầy đặn gồm đậu hũ chiên ngoài giòn trong mềm nóng hổi, chả cốm dẻo thơm ròn rã, thịt chân giò luộc mỏng tinh tế kèm mắm tôm chưng sủi bọt pha chanh quất ớt cay nồng.",
            reviews = listOf(
                Review("Gia Bảo", 5.0, "Mắm tôm ở đây pha cực kỳ ngon sủi bọt dậy mùi thơm, lòng dồi chiên giòn béo ngậy. Đậu rán giòn vỏ ăn rất đã.", "5 ngày trước"),
                Review("Kim Ngân", 4.0, "Quán nằm ngay ngõ Trạm, chỗ ngồi sạch sẽ thoải mái. Nem rán Hà Nội ăn kèm bún đậu rất giòn và ngon.", "3 tuần trước")
            )
        ),
        Restaurant(
            id = "rest_10",
            name = "Phở Gia Truyền Bát Đàn",
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad8cd305?q=80&w=600&auto=format&fit=crop",
            cuisine = "Vietnamese / Pho",
            rating = 4.6,
            reviewsCount = 3500,
            priceRange = "$$",
            distance = 0.4,
            address = "49 Bát Đàn, Hoàn Kiếm, Hà Nội",
            description = "Quán phở gia truyền xếp hàng trứ danh đất Hà Thành. Nước dùng trong vắt ngọt lịm tự nhiên từ xương ống ninh nhừ, bánh phở mỏng dai kết hợp thịt bò tái, chín tươi ngon chuẩn vị cổ truyền.",
            reviews = listOf(
                Review("Đăng Khoa", 5.0, "Hương vị phở chuẩn Hà Nội xưa, xếp hàng tự bê bát nhưng hoàn toàn xứng đáng. Bò chín thơm dẻo nước dùng trong thanh ngọt.", "6 ngày trước"),
                Review("Mỹ Linh", 4.5, "Phở ở đây ngon có tiếng từ lâu. Hãy gọi thêm quẩy giòn nhúng vào bát nước dùng nóng hổi để thưởng thức trọn vẹn vị ngon.", "2 tuần trước")
            )
        )
    )
}
