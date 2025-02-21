from typing import List, Dict
from collections import Counter
from src.db.models import BusinessDetails
from src.api_schemas import BusinessRecommendation, RewardModel
import random

class BusinessRecommendationService:
    def get_recommendations(self, user_businesses: List[BusinessDetails], all_businesses: List[BusinessDetails]) -> List[BusinessRecommendation]:
        """Get top 3 business recommendations based on user's most frequented categories."""
        if not user_businesses or not all_businesses:
            return []
            
        user_business_ids = {business.id for business in user_businesses}
        available_businesses = [b for b in all_businesses if b.id not in user_business_ids]
        
        if not available_businesses:
            return []

        top_categories = self._get_top_user_categories(user_businesses)
        businesses_by_category = self._group_businesses_by_category(available_businesses)
        return self._generate_recommendations(top_categories, businesses_by_category)

    def _get_top_user_categories(self, user_businesses: List[BusinessDetails], max_categories: int = 3) -> List[str]:
        """Find the most frequent categories in user's business history."""
        categories = [b.description for b in user_businesses if b.description]
        if not categories:
            return []
            
        category_counts = Counter(categories)
        return [cat for cat, _ in category_counts.most_common(max_categories)]

    def _group_businesses_by_category(self, businesses: List[BusinessDetails]) -> Dict[str, List[BusinessDetails]]:
        """Group businesses by their category (description)."""
        categorized = {}
        for business in businesses:
            if not business.description or not business.rewards:
                continue
                
            if business.description not in categorized:
                categorized[business.description] = []
            categorized[business.description].append(business)
        return categorized

    def _generate_recommendations(self, top_categories: List[str], 
                                businesses_by_category: Dict[str, List[BusinessDetails]]) -> List[BusinessRecommendation]:
        """Generate recommendations for each category."""
        recommendations = []
        for category in top_categories:
            if category in businesses_by_category:
                recommendation = self._get_random_recommendation(businesses_by_category[category])
                if recommendation:
                    recommendations.append(recommendation)
                    if len(recommendations) >= 3:
                        break
                        
        return recommendations

    def _get_random_recommendation(self, businesses: List[BusinessDetails]) -> BusinessRecommendation | None:
        """Get a random business and reward from a list of businesses."""
        businesses_to_try = random.sample(businesses, len(businesses))
        
        for business in businesses_to_try:
            if business.rewards:
                reward = random.choice(business.rewards)
                reward_model = RewardModel(
                    id=reward.id,
                    name=reward.name,
                    business_id=reward.business_id,
                    usage_count=reward.usage_count,
                    description=reward.description,
                    required_points=reward.required_points,
                    valid_from_timestamp=reward.valid_from_timestamp,
                    valid_until_timestamp=reward.valid_until_timestamp
                )
                return BusinessRecommendation(
                    business_name=business.business_name,
                    reward=reward_model
                )
        return None 