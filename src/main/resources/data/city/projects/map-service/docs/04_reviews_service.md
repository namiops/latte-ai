# Reviews Service

## Overview

The Reviews service is a service developed within the Utility Team's Map Services and is used to manage the reviews that are written for Places that are managed by the Places Service. The reviews service supports the writing of a textual review along with a rating. A user of the review service can only write a single review per place and is not allowed to write a review for places for which they are also the owner.

### Written reviews

A review should always consist a textual description that describes the experience of a visit to the related place. A review text cannot be empty and should not exceed 255 characters. The text of the review is currently not moderated or processed in any way.

### Unique and unbiased reviews

To prevent the automatic generation or spamming of reviews for a place the uniqueness of a review is enforced by allowing only a single review to be written per user per place. This requires that users are logged in and thus does not allow the creation of anonymous reviews. Additionally, to prevent owners from reviewing their own place we have enforced the rules that users cannot write reviews for places for which they are also registered as owners.

### Review ratings

Aside from a textual description to describe the visit to a place the Reviews service also requires the additional of a numeric rating, a score, to describe the overall experience. The Reviews service allows for a number between 0 and 10 to be assigned to a review. This score can be mapped by to a percentage, stars or numeric value in a front-end application that uses the Reviews service.

### API Specification
https://developer.woven-city.toyota/catalog/default/api/review-service-api/definition


