package com.example.data.local

data class AlphabetItem(
    val uppercase: Char,
    val lowercase: Char,
    val phoneticHintFa: String,
    val sampleWord: String,
    val sampleMeaningFa: String,
)

data class GrammarCard(
    val titleEn: String,
    val titleFa: String,
    val ruleEn: String,
    val ruleFa: String,
    val exampleEn: String,
    val exampleFa: String,
)

data class ReadingStory(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val lines: List<Pair<String, String>>,
)

data class WritingPrompt(
    val id: String,
    val promptFa: String,
    val promptEn: String,
    val targetWords: List<String>,
    val hintTranslationFa: String,
)

data class ConversationLine(
    val isSpeakerA: Boolean,
    val english: String,
    val persian: String,
)

data class ConversationScenario(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val lines: List<ConversationLine>,
)

object SkillModulesContent {

    val alphabet: List<AlphabetItem> = listOf(
        AlphabetItem('A', 'a', "اِی", "Apple", "سیب"),
        AlphabetItem('B', 'b', "بی", "Book", "کتاب"),
        AlphabetItem('C', 'c', "سی", "Coffee", "قهوه"),
        AlphabetItem('D', 'd', "دی", "Day", "روز"),
        AlphabetItem('E', 'e', "ای", "English", "انگلیسی"),
        AlphabetItem('F', 'f', "اِف", "Friend", "دوست"),
        AlphabetItem('G', 'g', "جی", "Good", "خوب"),
        AlphabetItem('H', 'h', "اِیچ", "Hello", "سلام"),
        AlphabetItem('I', 'i', "آی", "Idea", "ایده"),
        AlphabetItem('J', 'j', "جِی", "Juice", "آبمیوه"),
        AlphabetItem('K', 'k', "کِی", "Kind", "مهربان"),
        AlphabetItem('L', 'l', "اِل", "Learn", "یاد گرفتن"),
        AlphabetItem('M', 'm', "اِم", "Morning", "صبح"),
        AlphabetItem('N', 'n', "اِن", "Name", "نام"),
        AlphabetItem('O', 'o', "اُ", "Owl", "جغد"),
        AlphabetItem('P', 'p', "پی", "Please", "لطفاً"),
        AlphabetItem('Q', 'q', "کیو", "Quiet", "آرام"),
        AlphabetItem('R', 'r', "آر", "Read", "خواندن"),
        AlphabetItem('S', 's', "اِس", "Study", "مطالعه"),
        AlphabetItem('T', 't', "تی", "Teacher", "معلم"),
        AlphabetItem('U', 'u', "یو", "Understand", "فهمیدن"),
        AlphabetItem('V', 'v', "وی", "Voice", "صدا"),
        AlphabetItem('W', 'w', "دابیلیو", "Water", "آب"),
        AlphabetItem('X', 'x', "اِکس", "Box", "جعبه"),
        AlphabetItem('Y', 'y', "وای", "Yes", "بله"),
        AlphabetItem('Z', 'z', "زی", "Zero", "صفر"),
    )

    val grammarCards: List<GrammarCard> = listOf(
        GrammarCard(
            titleEn = "1. Subject + To Be (am / is / are)",
            titleFa = "۱. فاعل + فعل بودن (am / is / are)",
            ruleEn = "Use \"am\" with I, \"is\" with he/she/it, and \"are\" with you/we/they.",
            ruleFa = "برای I از am، برای he/she/it از is و برای you/we/they از are استفاده می‌کنیم.",
            exampleEn = "I am fine. / My name is Sara.",
            exampleFa = "من خوب هستم. / نام من سارا است.",
        ),
        GrammarCard(
            titleEn = "2. Simple Present Habits",
            titleFa = "۲. زمان حال ساده برای کارهای روزمره",
            ruleEn = "Use the base verb with I/you/we/they to describe daily routines.",
            ruleFa = "برای بیان کارهای تکراری روزانه با I/you/we/they از شکل ساده فعل استفاده کنید.",
            exampleEn = "I study English every morning.",
            exampleFa = "من هر روز صبح انگلیسی می‌خوانم.",
        ),
        GrammarCard(
            titleEn = "3. Polite Requests with \"Please\"",
            titleFa = "۳. درخواست مؤدبانه با «Please»",
            ruleEn = "Place \"please\" at the end or beginning of a request to sound natural and polite.",
            ruleFa = "قرار دادن «please» در ابتدا یا انتهای جمله، درخواست شما را محترمانه می‌کند.",
            exampleEn = "One coffee, please.",
            exampleFa = "لطفاً یک قهوه.",
        ),
        GrammarCard(
            titleEn = "4. Possessive \"My\" & \"Your\"",
            titleFa = "۴. صفات ملکی «My» و «Your»",
            ruleEn = "\"My\" shows something belongs to me; \"Your\" shows it belongs to you.",
            ruleFa = "«My» به معنی «مال من» و «Your» به معنی «مال شما» است.",
            exampleEn = "My mother and your friend.",
            exampleFa = "مادر من و دوست شما.",
        ),
    )

    val readingStories: List<ReadingStory> = listOf(
        ReadingStory(
            id = "story_1",
            titleEn = "Morning at the Café",
            titleFa = "صبح در کافه",
            lines = listOf(
                "Good morning! My name is Sara." to "صبح بخیر! نام من سارا است.",
                "I go to a small café today." to "امروز به یک کافه کوچک می‌روم.",
                "I order one coffee and fresh bread." to "یک قهوه و نان تازه سفارش می‌دهم.",
                "The bread is warm and delicious." to "نان گرم و خوشمزه است.",
            )
        ),
        ReadingStory(
            id = "story_2",
            titleEn = "My Study Day",
            titleFa = "روز مطالعه من",
            lines = listOf(
                "In the morning, I drink water and study English." to "صبح‌ها آب می‌نوشم و انگلیسی می‌خوانم.",
                "My teacher is kind and helps me." to "معلم من مهربان است و به من کمک می‌کند.",
                "At night, I say good night to my family." to "شب به خانواده‌ام شب بخیر می‌گویم.",
                "Then I sleep well!" to "و بعد خوب می‌خوابم!",
            )
        ),
    )

    val writingPrompts: List<WritingPrompt> = listOf(
        WritingPrompt(
            id = "w_1",
            promptFa = "جمله بساز: «نام من سارا است.»",
            promptEn = "Build: \"My name is Sara\"",
            targetWords = listOf("My", "name", "is", "Sara"),
            hintTranslationFa = "با My شروع کن",
        ),
        WritingPrompt(
            id = "w_2",
            promptFa = "جمله بساز: «لطفاً دو فنجان قهوه.»",
            promptEn = "Build: \"Two coffee please\"",
            targetWords = listOf("Two", "coffee", "please"),
            hintTranslationFa = "ابتدا عدد را بگذار",
        ),
        WritingPrompt(
            id = "w_3",
            promptFa = "جمله بساز: «من هر روز انگلیسی می‌خوانم.»",
            promptEn = "Build: \"I study English today\"",
            targetWords = listOf("I", "study", "English", "today"),
            hintTranslationFa = "با فاعل I شروع کن",
        ),
    )

    val conversations: List<ConversationScenario> = listOf(
        ConversationScenario(
            id = "conv_1",
            titleEn = "Meeting a New Friend",
            titleFa = "آشنایی با یک دوست جدید",
            lines = listOf(
                ConversationLine(true, "Hello! My name is Alex.", "سلام! نام من الکس است."),
                ConversationLine(false, "Hi Alex! My name is Sara. Nice to meet you.", "سلام الکس! نام من سارا است. از آشنایی با تو خوشوقتم."),
                ConversationLine(true, "How are you today?", "امروز حالت چطوره؟"),
                ConversationLine(false, "I am fine, thank you!", "من خوبم، ممنونم!"),
            ),
        ),
        ConversationScenario(
            id = "conv_2",
            titleEn = "Ordering at a Café",
            titleFa = "سفارش دادن در کافه",
            lines = listOf(
                ConversationLine(true, "Good morning! May I see the menu, please?", "صبح بخیر! ممکن است لطفاً منو را ببینم؟"),
                ConversationLine(false, "Yes, of course! Here you go.", "بله، حتماً! بفرمایید."),
                ConversationLine(true, "One coffee and bread, please.", "لطفاً یک قهوه و نان."),
                ConversationLine(false, "Thank you! You're welcome.", "متشکرم! خواهش می‌کنم."),
            ),
        ),
    )
}
