package net.oddpoet.mesos.http.dto

/**
 * This message is used by the C++ Scheduler HTTP API library as the return
 * type of the `call()` method. The message includes the HTTP status code with
 * which the master responded, and optionally a `scheduler::Response` message.
 *
 * There are three cases to consider depending on the HTTP response status code:
 *
 *  (1) '202 ACCEPTED': Indicates the call was accepted for processing and
 *        neither `response` nor `error` will be set.
 *
 *  (2) '200 OK': Indicates the call completed successfully, and the `response`
 *        field will be set if the `scheduler::Call::Type` has a corresponding
 *        `scheduler::Response::Type`; `error` will not be set.
 *
 *  (3) For all other HTTP status codes, the `response` field will not be set
 *      and the `error` field may be set to provide more information.
 *
 * NOTE: This message is used by the C++ Scheduler HTTP API library and is not
 * part of the API specification.
 */
data class APIResult(
        /**
         * HTTP status code with which the master responded.
         */
        val statusCode: Int,
        /**
         * This field will only be set if the call completed successfully and the
         * master responded with `200 OK` and a non-empty body.
         */
        val response: Response?,
        /**
         * This field will only be set if the call did not complete successfully and
         * the master responded with a status other than `202 Accepted` or `200 OK`,
         * and with a non-empty body.
         */
        val error: String?)